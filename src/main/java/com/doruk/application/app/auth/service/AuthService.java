package com.doruk.application.app.auth.service;

import com.doruk.application.app.auth.dto.*;
import com.doruk.application.enums.OtpChannel;
import com.doruk.application.enums.TemplateType;
import com.doruk.application.events.OtpDeliveryEvent;
import com.doruk.application.exception.IncompleteStateException;
import com.doruk.application.exception.InvalidCredentialException;
import com.doruk.application.exception.TooManyAttemptsException;
import com.doruk.application.interfaces.EventPublisher;
import com.doruk.application.interfaces.MemoryStorage;
import com.doruk.application.security.PasswordEncoder;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.infrastructure.config.AppConfig;
import com.doruk.infrastructure.persistence.auth.AuthRepository;
import com.doruk.infrastructure.security.JwtIssuer;
import com.doruk.infrastructure.util.Constants;
import com.doruk.infrastructure.util.GenerateRandom;
import com.doruk.infrastructure.util.KeyNamespace;
import com.doruk.infrastructure.util.StringUtil;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

@Singleton
@RequiredArgsConstructor
public class AuthService {
    private final JwtIssuer issuer;
    private final AppConfig appConfig;
    private final AuthRepository authRepo;
    private final PasswordEncoder hasher;
    private final EventPublisher eventPublisher;
    private final UserAgentAnalyzer uaa;
    private final PasswordEncoder passwordEncoder;
    private final MemoryStorage storage;
    private final AppConfig config;
    private final LoginHelper loginHelper;

    private final Map<MultiAuthType, Function<AuthDto, LoginResponse>> authInitializers = Map.of(
            MultiAuthType.PHONE, this::initPhoneFactorAuth,
            MultiAuthType.EMAIL, this::initEmailFactorAuth
    );

    private LoginResponse createMfaResponse(AuthDto user, String mfaToken) {
        return LoginResponse.builder()
                .isEmailVerified(user.emailVerified())
                .isPhoneVerified(user.phoneVerified())
                .mfaRequired(true)
                .mfaType(user.multiFactorAuth())
                .mfaToken(mfaToken)
                .mfaExpiresIn(Constants.MFA_VALIDITY_SECONDS)
                .build();
    }

    private String createAndPublishOtpExTransaction(
            String prefix, Duration duration, boolean createMagic,
            OtpTransaction.OtpTransactionBuilder transBuilder, TemplateType templateType) {
        // should return transactionId, tid separately (tid = internal id, sent to user, transactionId = full redis id)
        var cooldownDuration = Duration.ofSeconds(Constants.RESEND_OTP_COOLDOWN_SECONDS);
        var otp = GenerateRandom.generateOtp();
        var tid = GenerateRandom.generateTransactionId();

        var transactionId = KeyNamespace.getNamespacedId(prefix, tid);

        // complete the builder
        var txn = transBuilder.otp(otp).build();

        // create attempts, cooldown
        storage.saveEx(KeyNamespace.getNamespacedId(prefix, transactionId), txn, duration);
        storage.saveEx(KeyNamespace.cooldownPrefix(prefix, tid), Boolean.TRUE, cooldownDuration);
        storage.saveEx(KeyNamespace.attemptPrefix(prefix, tid), 0, duration);

        var event = OtpDeliveryEvent.builder()
                .to(txn.target())
                .channel(txn.channel())
                .otp(otp)
                .contentTemplate(templateType);

        if (!createMagic) {
            eventPublisher.publish(event.build());
            return tid;
        }

        var magicSuffix = GenerateRandom.generateSessionId();
        var magicLink = StringUtil.generateUrl(config, magicSuffix);

        event.magicLink(magicLink);

        // save magic link
        storage.saveEx(KeyNamespace.magicLinkPrefix(prefix, magicSuffix), transactionId, duration);
        eventPublisher.publish(event.build());

        return tid;
    }

    private void resendExTransactionOtp(String prefix, String tid) {
        var invalidException = new InvalidCredentialException("Invalid or Expired transaction session");

        var txn = storage.get(KeyNamespace.getNamespacedId(prefix, tid), OtpTransaction.class)
                .orElseThrow(() -> invalidException);

        storage.get(KeyNamespace.cooldownPrefix(prefix, tid), Boolean.class)
                .ifPresent(_ -> {
                    throw new TooManyAttemptsException("Cooling down, sit back and relax for a while.");
                });

        // finally re-publish the event
        eventPublisher.publish(OtpDeliveryEvent.builder()
                .to(txn.target())
                .channel(txn.channel())
                .otp(txn.otp())
                .contentTemplate(TemplateType.GENERIC)
                .build());

        // reset the cooldown
        storage.saveEx(KeyNamespace.cooldownPrefix(prefix, tid), Boolean.TRUE,
                Duration.ofSeconds(Constants.RESEND_OTP_COOLDOWN_SECONDS));
    }

    private void deleteOtpExTransaction(String prefix, String tid) {
        storage.delete(KeyNamespace.getNamespacedId(prefix, tid)); // main transaction object
        storage.delete(KeyNamespace.cooldownPrefix(prefix, tid));
        storage.delete(KeyNamespace.attemptPrefix(prefix, tid));
    }

    private void deleteOtpExTransactionFromMagic(String prefix, String magicSuffix, String transactionId) {
        storage.delete(KeyNamespace.getNamespacedId(prefix, magicSuffix)); // magic link
        this.deleteOtpExTransaction(prefix, KeyNamespace.extractTid(prefix, transactionId)); // main transaction object
    }

    private void limitOtpAttempts(String prefix, String tid, int limit) {
        var attempts = storage.increment(KeyNamespace.attemptPrefix(prefix, tid));
        if (attempts >= limit) {
            this.deleteOtpExTransaction(prefix, tid);
            throw new TooManyAttemptsException("Too Many Attempts");
        }
    }

    private LoginResponse createMfaTransaction(AuthDto user, String target, OtpChannel channel) {
        var duration = Duration.ofSeconds(Constants.MFA_VALIDITY_SECONDS);
        var tid = this.createAndPublishOtpExTransaction(
                KeyNamespace.mfaTransaction(),
                duration,
                false,
                OtpTransaction.builder()
                        .userId(user.id())
                        .target(target)
                        .channel(channel),
                TemplateType.MFA);

        return createMfaResponse(user, tid);
    }

    private LoginResponse initPhoneFactorAuth(AuthDto user) {
        return createMfaTransaction(user, user.phone(), OtpChannel.PHONE);
    }

    private LoginResponse initEmailFactorAuth(AuthDto user) {
        return createMfaTransaction(user, user.email(), OtpChannel.EMAIL);
    }

    private String createAndPublishEmailVerificationTransaction(String userId, String email) {
        var duration = Duration.ofSeconds(Constants.MAGIC_LINK_VALIDITY_SECONDS);
        return this.createAndPublishOtpExTransaction(
                KeyNamespace.verificationTransaction(),
                duration,
                true,
                OtpTransaction.builder()
                        .target(email)
                        .userId(userId)
                        .channel(OtpChannel.EMAIL),
                TemplateType.EMAIL_VERIFICATION);
    }

    private String createAndPublishPhoneVerificationTransaction(String userId, String phone) {
        var duration = Duration.ofSeconds(Constants.OTP_VALIDITY_SECONDS);
        return this.createAndPublishOtpExTransaction(
                KeyNamespace.verificationTransaction(),
                duration,
                false,
                OtpTransaction.builder()
                        .target(phone)
                        .userId(userId)
                        .channel(OtpChannel.PHONE),
                TemplateType.PHONE_VERIFICATION);
    }

    private String createAndPublishAuthUpdateTransaction(String userId, String payload, OtpChannel type) {
        var duration = Duration.ofSeconds(Constants.AUTH_UPDATE_VALIDITY_SECONDS);
        return this.createAndPublishOtpExTransaction(
                KeyNamespace.updateAuthTransaction(),
                duration,
                false,
                OtpTransaction.builder()
                        .target(payload)
                        .payload(payload)
                        .channel(type)
                        .userId(userId),
                TemplateType.GENERIC
        );
    }

    private void verifyAuthUpdateTransaction(String userId, String tid, int otp, OtpChannel channel) {
        var invalidCredentials = new InvalidCredentialException("Expired or Invalid otp session");
        var prefix = KeyNamespace.updateAuthTransaction();

        var txn = storage.get(KeyNamespace.getNamespacedId(prefix, tid), OtpTransaction.class)
                .orElseThrow(() -> invalidCredentials);
        if (!(
                txn.channel() == channel &&
                        txn.userId().equalsIgnoreCase(userId)
        ))
            throw invalidCredentials;

        // check for attempts
        this.limitOtpAttempts(prefix, tid, Constants.AUTH_UPDATE_ATTEMPT_LIMIT);

        if (txn.otp() != otp)
            throw invalidCredentials;

        switch (channel) {
            case EMAIL -> authRepo.updateEmail(txn.userId(), txn.payload(), true);
            case PHONE -> authRepo.updatePhone(txn.userId(), txn.payload(), true);
        }
    }

    public LoginResponse performLogin(String identifier, String password, DeviceInfoObject deviceInfoObject) {
        var invalidCredentials = new InvalidCredentialException("Incorrect username/email or password.");

        var user = authRepo.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> invalidCredentials);

        if (!hasher.matches(password, user.password()))
            throw invalidCredentials;

        // check for multi factor auth
        if (user.multiFactorAuth() != MultiAuthType.NONE)
            return this.authInitializers.get(user.multiFactorAuth()).apply(user);

        return loginHelper.createLoginResponse(deviceInfoObject.deviceId(), deviceInfoObject.deviceInfo(uaa), user);
    }

    public LoginResponse performMfa(String mfaToken, int otp, DeviceInfoObject deviceInfoObject) {
        String prefix = KeyNamespace.mfaTransaction();
        var mfaTransaction = storage.get(KeyNamespace.getNamespacedId(prefix, mfaToken), OtpTransaction.class)
                .orElseThrow(() -> new InvalidCredentialException("Invalid or expired MFA session."));

        // increment the attempt
        this.limitOtpAttempts(prefix, mfaToken, Constants.MFA_ATTEMPT_LIMIT);

        if (mfaTransaction.otp() != otp)
            throw new InvalidCredentialException("Invalid otp code");

        // create session
        var response = loginHelper.createLoginResponse(deviceInfoObject.deviceId(), deviceInfoObject.deviceInfo(uaa),
                authRepo.findByUserId(mfaTransaction.userId()).orElseThrow());

        // remove the mfa transaction
        this.deleteOtpExTransaction(prefix, mfaToken);
        return response;
    }

    public JwtResponse refreshAccessToken(String sessionId) {
        var invalidException = new InvalidCredentialException("Invalid or expired session. Please login again.");
        var session = authRepo.getActiveSession(sessionId)
                .orElseThrow(() -> invalidException);

        return issuer.issueToken(new JwtRequest(
                session.userId(),
                appConfig.appId(),
                session.permissions()));
    }

    public List<SessionDto> listSessions(String userId) {
        return authRepo.getActiveDevices(userId);
    }

    public void logoutCurrent(String sessionId) {
        authRepo.deleteSession(sessionId);
    }

    public void logoutAll(String userId, boolean deleteBiometrics) {
        authRepo.deleteAllSessions(userId, deleteBiometrics);
    }

    public void logoutOthers(String userId, String sessionId, boolean deleteBiometrics) {
        authRepo.deleteOtherSessions(userId, sessionId, deleteBiometrics);
    }

    public void updatePassword(String userId, String password, String newPassword) {
        var currentPassword = authRepo.getUserPassword(userId);
        if (!passwordEncoder.matches(password, currentPassword))
            throw new InvalidCredentialException("Incorrect current password");

        authRepo.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    public Map<String, String> initEmailVerification(String userId) {
        var user = authRepo.getUserEmail(userId);
        var tid = this.createAndPublishEmailVerificationTransaction(userId, user.getKey());
        return Map.of("tid", tid, "message", "OTP is sent to your email address");
    }

    // from otp, and from magic link
    public void verifyEmail(String userId, String tid, int otp) {
        var expiredException = new InvalidCredentialException("Invalid or expired verification session.");
        var prefix = KeyNamespace.verificationTransaction();

        var transaction = storage.get(KeyNamespace.getNamespacedId(prefix, tid),
                OtpTransaction.class).orElseThrow(() -> expiredException);

        // check if the user id matches
        if (!transaction.userId().equals(userId))
            throw expiredException;

        // finally proceed with otp matching and attempt counts.
        this.limitOtpAttempts(prefix, tid, Constants.MFA_ATTEMPT_LIMIT);

        // check if the otp matches
        if (transaction.otp() != otp)
            throw expiredException;

        // finally validate the user
        authRepo.verifyUserEmail(userId);
        this.deleteOtpExTransaction(prefix, tid);
    }

    public void verifyEmail(String magicLinkPointer) {
        var expiredException = new InvalidCredentialException("Invalid or expired verification session.");
        var prefix = KeyNamespace.verificationTransaction();
        var pointer = KeyNamespace.magicLinkPrefix(prefix, magicLinkPointer);
        // fetch the transaction id
        var transactionId = storage.get(pointer, String.class)
                .orElseThrow(() -> expiredException);

        // check if the transaction exists, in case of otp attempt limit
        var transaction = storage.get(transactionId,
                OtpTransaction.class).orElseThrow(() -> {
            // delete the pointer
            storage.delete(pointer);
            return expiredException;
        });

        // finally validate the user
        authRepo.verifyUserEmail(transaction.userId());
        this.deleteOtpExTransactionFromMagic(prefix, magicLinkPointer, transactionId);
    }

    public Map<String, String> initPhoneVerification(String userId) {
        var phonePair = authRepo.getUserPhone(userId);
        var phone = phonePair.getKey();

        if (phone == null || phone.isBlank())
            throw new IncompleteStateException("Phone number not provided, please update your profile.");
        var tid = this.createAndPublishPhoneVerificationTransaction(userId, phone);
        return Map.of("tid", tid, "message", "OTP is sent to your phone number");
    }

    // from otp only
    public void verifyPhone(String tid, int otp) {
        var expiredException = new InvalidCredentialException("Invalid or expired otp session");
        var prefix = KeyNamespace.verificationTransaction();
        var transaction = storage.get(KeyNamespace.getNamespacedId(prefix, tid),
                OtpTransaction.class).orElseThrow(() -> expiredException);

        // check for attempts
        this.limitOtpAttempts(prefix, tid, Constants.MFA_ATTEMPT_LIMIT);

        // check otp
        if (transaction.otp() != otp)
            throw expiredException;

        // finally validate the user
        authRepo.verifyUserPhone(transaction.userId());
        this.deleteOtpExTransaction(prefix, tid);
    }

    // ~~~~~~~~~Update Email / Phone~~~~~~~~~

    public AuthUpdateResponse updateEmail(String userId, String email) {
        var current = authRepo.getUserEmail(userId);
        if (!current.getValue()) {
            authRepo.updateEmail(userId, email, false);
            return new AuthUpdateResponse(null, false,
                    "Email address updated, please proceed to verify it.");
        }

        var tid = createAndPublishAuthUpdateTransaction(userId, email, OtpChannel.EMAIL);

        return new AuthUpdateResponse(
                tid,
                true,
                "OTP is sent to your new email address, please enter the OTP to complete the process."
        );
    }

    public AuthUpdateResponse updatePhone(String userId, String phone) {
        var current = authRepo.getUserPhone(userId);
        if (!current.getValue()) {
            authRepo.updateEmail(userId, phone, false);
            return new AuthUpdateResponse(null, false,
                    "Phone Number updated, please proceed to verify it.");
        }

        var tid = createAndPublishAuthUpdateTransaction(userId, phone, OtpChannel.PHONE);

        return new AuthUpdateResponse(tid, true,
                "OTP is sent to your new phone number, please enter the OTP to complete the process.");
    }

    public void verifyUpdatePhoneTransaction(String userId, String tid, int otp) {
        verifyAuthUpdateTransaction(userId, tid, otp, OtpChannel.PHONE);
    }

    public void verifyUpdateEmailTransaction(String userId, String tid, int otp) {
        verifyAuthUpdateTransaction(userId, tid, otp, OtpChannel.EMAIL);
    }

    private String createAndPublishPasswordResetTransaction(String userId, String payload, boolean isPhone) {
        var duration = Duration.ofSeconds(Constants.PW_RESET_VALIDITY_SECONDS);
        var prefix = KeyNamespace.resetPasswordTransaction();

        return this.createAndPublishOtpExTransaction(
                prefix,
                duration,
                !isPhone,
                OtpTransaction.builder()
                        .target(payload)
                        .channel(isPhone ? OtpChannel.PHONE : OtpChannel.EMAIL)
                        .userId(userId),
                TemplateType.PASSWORD_RESET
        );
    }

    public Map<String, String> initPasswordReset(String identifier, boolean usePhone) {
        Map<String, String> successMsg = new HashMap<>();
        successMsg.put("message", "OTP sent to your " + (usePhone ? "phone number" : "email address"));

        var userOpt = authRepo.findByUsernameOrEmail(identifier);
        if (userOpt.isEmpty())
            return successMsg;
        var user = userOpt.get();

        if (usePhone && user.phone() == null)
            return successMsg;

        var tid = this.createAndPublishPasswordResetTransaction(user.id(), user.phone(), usePhone);

        successMsg.put("tid", tid);
        return successMsg;
    }

    public void verifyAndResetPasswordOtp(String tid, int otp, String password) {
        var invalidException = new InvalidCredentialException("Invalid or Expired otp session");
        var prefix = KeyNamespace.resetPasswordTransaction();

        var txn = storage.get(KeyNamespace.getNamespacedId(prefix, tid), OtpTransaction.class)
                .orElseThrow(() -> invalidException);

        this.limitOtpAttempts(prefix, tid, Constants.PW_UPDATE_OTP_ATTEMPT_LIMIT);

        if (txn.otp() != otp)
            throw invalidException;

        // update the password
        authRepo.updatePassword(txn.userId(), passwordEncoder.encode(password));

        // delete txn
        this.deleteOtpExTransaction(prefix, tid);
    }

    public void verifyAndResetPasswordMagic(String magicLink, String password) {
        var invalidException = new InvalidCredentialException("Invalid or Expired otp session");
        var prefix = KeyNamespace.resetPasswordTransaction();

        var reference = storage.get(KeyNamespace.magicLinkPrefix(prefix, magicLink), String.class)
                .orElseThrow(() -> invalidException);

        var txn = storage.get(reference, OtpTransaction.class)
                .orElseThrow(() -> {
                    storage.delete(reference);
                    return invalidException;
                });

        // if txn exists, update the password now
        authRepo.updatePassword(txn.userId(), passwordEncoder.encode(password));

        this.deleteOtpExTransactionFromMagic(prefix, magicLink, reference);
    }

    // ~~~~~~~~~~RESEND OTP~~~~~~~~~~~~~~~~~~

    public void resendVerificationOtp(String tid) {
        this.resendExTransactionOtp(KeyNamespace.verificationTransaction(), tid);
    }

    public void resendMfaOtp(String tid) {
        this.resendExTransactionOtp(KeyNamespace.mfaTransaction(), tid);
    }

    public void resendAuthUpdateOtp(String tid) {
        this.resendExTransactionOtp(KeyNamespace.updateAuthTransaction(), tid);
    }

    public void resendPasswordResetOtp(String tid) {
        this.resendExTransactionOtp(KeyNamespace.resetPasswordTransaction(), tid);
    }

    // ~~~~~~~~~~~~~~~~~~~~ ENABLE / DISABLE MFA ~~~~~~~~~~~~~~~~~~~

    public void enableMfa(String userId, MultiAuthType authType) {
        // phone must not be null, check and through incomplete state
        var user = authType == MultiAuthType.EMAIL ?
                authRepo.getUserEmail(userId) :
                authRepo.getUserPhone(userId);

        var unverifiedMsg = "Please verify your " + (authType == MultiAuthType.PHONE ? "Phone" : "Email") +
                " to enable Multi Factor Authorization in your account";

        if (!user.getValue() || user.getKey() == null)
            throw new IncompleteStateException(unverifiedMsg);

        // enable the mfa
        authRepo.enableMfa(userId, authType);
    }

    public void disableMfa(String userId, String password) {
        var user = authRepo.findByUserId(userId).orElseThrow(() ->
                new IllegalStateException("Disable MFA: user not found"));

        if (!hasher.matches(password, user.password()))
            throw new InvalidCredentialException("Incorrect password");

        // check if mfa is enabled
        if (user.multiFactorAuth() == MultiAuthType.NONE)
            return;

        // disable the mfa
        authRepo.disableMfa(userId);
    }
}