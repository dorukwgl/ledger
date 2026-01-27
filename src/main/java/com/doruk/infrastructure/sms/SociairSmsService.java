package com.doruk.infrastructure.sms;

import com.doruk.application.enums.TemplateType;
import com.doruk.application.interfaces.SmsService;
import com.doruk.infrastructure.apiclient.SociairClient;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Singleton
@Requires(env = "prod")
public class SociairSmsService implements SmsService {
    private final SociairClient client;

    public SociairSmsService(SociairClient client) {
        this.client = client;

        // query and log the balance
        var balance = client.getBalance();
        System.out.println(balance);
    }

    @Override
    public void sendOtp(String phone, int otp, TemplateType type) {
        String template = SmsTemplates.getSmsTemplate(otp, type);
        client.sendSms(phone, template);
    }
    // phone can be comma separated multiple list of values, to send to multiple clients
}
