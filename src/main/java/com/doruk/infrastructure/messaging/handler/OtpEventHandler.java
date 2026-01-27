package com.doruk.infrastructure.messaging.handler;


import com.doruk.application.events.OtpDeliveryEvent;
import com.doruk.application.interfaces.MailService;
import com.doruk.application.interfaces.SmsService;
import com.doruk.infrastructure.config.AppExecutors;
import io.micronaut.nats.annotation.NatsListener;
import io.micronaut.nats.annotation.Subject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@Singleton
@NatsListener
@RequiredArgsConstructor
public class OtpEventHandler {
    private final SmsService smsService;
    private final MailService mailService;
    private final AppExecutors executors;

//    @Subject(value = "auth.mfa.sms-otp", queue = "otp-workers")
//    public void handleSmsOtp(SmsOtpDto dto) {
//        CompletableFuture.runAsync(() -> smsService.sendOtp(dto.phone(), dto.otp(), dto.templateType()),
//                        executors.VIRTUAL())
//                .join();
//    }
//
//    @Subject(value = "auth.mfa.email-otp", queue = "otp-workers")
//    public void handleEmailOtp(EmailOtpDto dto) {
//        CompletableFuture.runAsync(() -> {
//                    var user = userRepo.getMailAddress(dto.id());
//                    mailService.sendOtp(new MailService.MailParams(
//                            user.getKey(),
//                            user.getValue(),
//                            null,
//                            dto.otp()
//                    ), dto.templateType());
//                },
//                executors.VIRTUAL()
//        ).join();
//    }
//
//    @Subject(value = "auth.update.otp", queue = "update-otp-workers")
//    public void handleUpdateOtp(AuthUpdateTransaction dto) {
//        CompletableFuture.runAsync(() -> {
//            switch (dto.type()) {
//                case EMAIL ->
//                    mailService.sendOtp(
//                            new MailService.MailParams(
//                                    "",
//                                    dto.payload(),
//                                    null,
//                                    dto.otp()
//                            ), TemplateType.GENERIC
//                    );
//                case PHONE -> smsService.sendOtp(dto.payload(), dto.otp(), TemplateType.GENERIC);
//            }
//        }, executors.VIRTUAL()).join();
//    }

    @Subject(value = "event.otp.delivery", queue = "event-otp-workers")
    public void handleSendOtp(OtpDeliveryEvent dto) {
        CompletableFuture.runAsync(() -> {
            switch (dto.channel()) {
                case EMAIL ->
                    mailService.sendOtp(
                            new MailService.MailParams(
                                    "",
                                    dto.to(),
                                    dto.magicLink(),
                                    dto.otp()
                            ), dto.contentTemplate()
                    );
                case PHONE -> smsService.sendOtp(dto.to(), dto.otp(),  dto.contentTemplate());
            }
        },  executors.VIRTUAL()).join();
    }
}
