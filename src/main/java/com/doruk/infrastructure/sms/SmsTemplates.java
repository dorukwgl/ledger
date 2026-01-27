package com.doruk.infrastructure.sms;

import com.doruk.application.enums.TemplateType;

public class SmsTemplates {
    public static String getSmsTemplate(int otp, TemplateType type) {
        return switch (type) {
            case MFA ->
                    "YakshaSoft authentication code: " + otp + ". Valid for 3 mins. Do not share.";

            case PASSWORD_RESET ->
                    "YakshaSoft password reset code: " + otp + ". If this wasn't you, ignore.";

            case PHONE_VERIFICATION ->
                    "YakshaSoft: Your phone verification code is " + otp + ".";
            case GENERIC -> "YakshaSoft: Your OTP verification code is " + otp + ".";
            default -> throw new IllegalArgumentException("Invalid sms template type");
        };
    }
}
