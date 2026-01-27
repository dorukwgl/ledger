package com.doruk.application.app.auth.service;

import com.doruk.application.app.auth.dto.BiometricDto;
import com.doruk.application.app.auth.dto.BiometricTransaction;
import com.doruk.application.app.auth.dto.DeviceInfoObject;
import com.doruk.application.app.auth.dto.LoginResponse;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.exception.IncompleteStateException;
import com.doruk.application.exception.InvalidCredentialException;
import com.doruk.application.interfaces.MemoryStorage;
import com.doruk.infrastructure.config.AppExecutors;
import com.doruk.infrastructure.persistence.auth.AuthRepository;
import com.doruk.infrastructure.util.Constants;
import com.doruk.infrastructure.util.KeyNamespace;
import jakarta.inject.Singleton;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Singleton
@RequiredArgsConstructor
public class BiometricService {
    private final AuthRepository authRepo;
    private final MemoryStorage storage;
    private final AppExecutors appExecutors;
    private final UserAgentAnalyzer uaa;
    private final LoginHelper loginHelper;

    private byte[] validateAndGetPublicKey(String rawKey) {
        try {
            byte[] raw = Base64.getDecoder().decode(rawKey);

            if (raw.length != 64)
                throw new InvalidCredentialException("Invalid public key length");

            // reconstruct the EC public key
            byte[] xBytes = Arrays.copyOfRange(raw, 0, 32);
            byte[] yBytes = Arrays.copyOfRange(raw, 32, 64);

            BigInteger x = new BigInteger(1, xBytes);
            BigInteger y = new BigInteger(1, yBytes);

            ECPoint ecPoint = new ECPoint(x, y);

            // build P-256 parameters
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
            parameters.init(new ECGenParameterSpec("secp256r1"));
            ECParameterSpec p256 = parameters.getParameterSpec(ECParameterSpec.class);

            // create the public key
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            ECPublicKeySpec spec = new ECPublicKeySpec(ecPoint, p256);

            PublicKey key = keyFactory.generatePublic(spec);

            // perform algorithm and curve validations
            if (!(key instanceof ECPublicKey ecKey)) {
                throw new InvalidCredentialException("Unsupported key type: must be EC Public Key");
            }

            if (!"EC".equals(ecKey.getAlgorithm())) {
                throw new InvalidCredentialException("Unsupported key algorithm: must be EC");
            }

            if (!ecKey.getParams().getCurve().equals(p256.getCurve())) {
                throw new InvalidCredentialException("Unsupported curve: must be P-256");
            }

            return key.getEncoded();
        } catch (IllegalArgumentException | InvalidParameterSpecException | InvalidKeySpecException e) {
            throw new InvalidCredentialException("Invalid public key. Expected Format: Base64-encoded raw EC public key (X || Y)");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private BiometricDto validateTxnEligibility(Optional<BiometricDto> dto, String usrLock, String deviceLock) {
        var conflictException = new ConflictingArgumentException("Found an ongoing biometric transaction.");

        var biometric = dto.orElseThrow(() ->
                new IncompleteStateException("No enrolled biometrics found for the given device."));

        // check if txn already exists
        storage.get(KeyNamespace.getNamespacedId(usrLock, biometric.userId()), Boolean.class)
                .ifPresent(_ -> {
                    throw conflictException;
                });
        storage.get(KeyNamespace.getNamespacedId(deviceLock, biometric.deviceId()), Boolean.class)
                .ifPresent(_ -> {
                            throw conflictException;
                        }
                );

        return biometric;
    }

    private Pair<String, byte[]> generateChallenge() {
        var bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);

        var encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return new Pair<>(encoded, bytes);
    }

    private void removeBiometricTxn(String deviceId, String userId) {
        storage.delete(KeyNamespace.getNamespacedId(KeyNamespace.biometricTxnUserLock(), userId));
        storage.delete(KeyNamespace.getNamespacedId(KeyNamespace.biometricTxnDeviceLock(), deviceId));
        storage.delete(KeyNamespace.getNamespacedId(KeyNamespace.biometricTransaction(), deviceId));
    }

    private String createBiometricVerificationTxn(String deviceId, String ipContext) {
        var usrLock = KeyNamespace.biometricTxnUserLock();
        var deviceLock = KeyNamespace.biometricTxnDeviceLock();
        var txnPrefix = KeyNamespace.biometricTransaction();

        var biometric = this.validateTxnEligibility(
                authRepo.getActiveBiometric(deviceId), usrLock, deviceLock);

        var duration = Duration.ofSeconds(Constants.BIOMETRIC_TXN_VALIDITY_SECONDS);

        // generate challenge
        var challenge = this.generateChallenge();
        // create new transaction object
        var txn = new BiometricTransaction(
                biometric.userId(),
                biometric.publicKey(),
                challenge.getValue(),
                ipContext
        );

        // create txn and lock it
        storage.saveEx(KeyNamespace.getNamespacedId(usrLock, biometric.userId()), true, duration);
        storage.saveEx(KeyNamespace.getNamespacedId(deviceLock, biometric.deviceId()), true, duration);
        storage.saveEx(KeyNamespace.getNamespacedId(txnPrefix, biometric.deviceId()), txn, duration);

        // return the challenge
        return challenge.getKey();
    }

    private boolean verifySignature(byte[] publicKey, byte[] challenge, String encodedSignature) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                KeyFactory kf = KeyFactory.getInstance("EC");
                X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
                PublicKey key = kf.generatePublic(spec);

                // init verifier
                Signature verifier = Signature.getInstance("SHA256withECDSA");
                verifier.initVerify(key);
                verifier.update(challenge);

                // match the signature
                return verifier.verify(Base64.getUrlDecoder().decode(encodedSignature));
            } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
        }, appExecutors.CPU()).join();
    }

    public void enrollBiometrics(String deviceId, String userId, String publicKey) {
        authRepo.createOrUpdateBiometrics(deviceId, userId,
                this.validateAndGetPublicKey(publicKey));
    }

    public String initBiometrics(String deviceId, String ipContext) {
        return this.createBiometricVerificationTxn(deviceId, ipContext);
    }

    public LoginResponse performBiometrics(String deviceId, String notifId, String encodedChallenge, String encodedSignature, String ipContext, DeviceInfoObject deviceInfoObject) {
        var prefix = KeyNamespace.biometricTransaction();
        var invalidException = new InvalidCredentialException("Invalid or Expired biometric session");

        var txn = storage.get(KeyNamespace.getNamespacedId(prefix, deviceId), BiometricTransaction.class)
                .orElseThrow(() -> invalidException);

        // match ip
        if (!txn.ip().equals(ipContext)) {
            this.removeBiometricTxn(deviceId, txn.userId());
            throw invalidException;
        }

        // match challenge
        if (!Base64.getUrlEncoder().withoutPadding().encodeToString(txn.challenge())
                .equals(encodedChallenge)) {
            this.removeBiometricTxn(deviceId, txn.userId());
            authRepo.removeBiometric(deviceId); // tried to sniff, delete whole biometrics
            throw invalidException;
        }

        // validate the signature
        var valid = this.verifySignature(txn.publicKey(), txn.challenge(), encodedSignature);

        if (!valid) {
            this.removeBiometricTxn(deviceId, txn.userId());
            authRepo.removeBiometric(deviceId);
            throw invalidException;
        }

        this.removeBiometricTxn(deviceId, txn.userId());
        // update last used
        authRepo.updateLastUsedBiometric(deviceId);

        return loginHelper.createLoginResponse(Optional.of(notifId), deviceInfoObject.deviceInfo(uaa), authRepo.findByUserId(txn.userId()).orElseThrow());
    }
}
