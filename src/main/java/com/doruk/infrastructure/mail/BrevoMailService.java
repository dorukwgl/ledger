package com.doruk.infrastructure.mail;

import com.doruk.application.enums.TemplateType;
import com.doruk.application.interfaces.MailService;
import com.doruk.infrastructure.apiclient.BrevoClient;
import com.doruk.infrastructure.apiclient.dto.BrevoMailRequest;
import com.doruk.infrastructure.config.BrevoConfig;
import jakarta.inject.Singleton;
import javafx.util.Pair;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Singleton
public class BrevoMailService implements MailService {
    private final BrevoClient client;
    private final BrevoConfig brevoConfig;

    @Override
    public void sendOtp(MailParams mailParams, TemplateType type) {
        Pair<String, String> template = switch (type) {
            case MFA -> new Pair<>("Multi Factor Authentication", Templates.mfaTemplate());
            case EMAIL_VERIFICATION -> new Pair<>("Email Verification", Templates.emailVerificationTemplate());
            case PASSWORD_RESET -> new Pair<>("Password Reset", Templates.passwordResetTemplate());
            case GENERIC -> new Pair<>("OTP Verification", Templates.genericTemplate());
            default -> throw new IllegalArgumentException("Invalid template type");
        };

        var request = new BrevoMailRequest(
                new BrevoMailRequest.MailUser(brevoConfig.senderName(), brevoConfig.senderEmail()),
                List.of(new BrevoMailRequest.MailUser(mailParams.toName(), mailParams.toEmail())),
                template.getKey(),
                template.getValue(),
                Map.of(
                        "otp", mailParams.otp(), "name",
                        mailParams.toName(),
                        "url", type == TemplateType.PASSWORD_RESET ?
                                mailParams.tempUrl() : ""
                )
        );
        client.sendEmail(brevoConfig.apiKey(), request);
    }
}
