package com.doruk.application.interfaces;

import com.doruk.application.enums.TemplateType;

public interface SmsService {
    void sendOtp(String phone, int otp, TemplateType type);
}
