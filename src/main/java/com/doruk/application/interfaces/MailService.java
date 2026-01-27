package com.doruk.application.interfaces;

import com.doruk.application.enums.TemplateType;

public interface MailService {
    record MailParams(String toName, String toEmail, String tempUrl, int otp){};

    void sendOtp(MailParams params, TemplateType type);
}
