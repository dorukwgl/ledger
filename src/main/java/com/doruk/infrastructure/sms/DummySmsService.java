package com.doruk.infrastructure.sms;

import com.doruk.application.enums.TemplateType;
import com.doruk.application.interfaces.SmsService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(env = "dev")
@Bean(typed = SmsService.class)
public class DummySmsService implements SmsService {
    @Override
    public void sendOtp(String phone, int otp, TemplateType type) {
        String template = SmsTemplates.getSmsTemplate(otp, type);

        IO.println(template);
    }
}
