package com.doruk.infrastructure.util;

public final class Constants {
    public final static int MFA_VALIDITY_SECONDS = 120;
    public final static int MFA_ATTEMPT_LIMIT = 2;
    public final static int OTP_VALIDITY_SECONDS = 120;
    public final static int MAGIC_LINK_VALIDITY_SECONDS = 900;
    public final static String SESSION_COOKIE_HEADER = "X-State-Transfer";
    public final static int AUTH_UPDATE_VALIDITY_SECONDS = 180;
    public final static int AUTH_UPDATE_ATTEMPT_LIMIT = 2;
    public final static int PW_UPDATE_OTP_ATTEMPT_LIMIT = 2;
    public final static int RESEND_OTP_COOLDOWN_SECONDS = 40;
    public final static int PW_RESET_VALIDITY_SECONDS = 300;
    public final static int BIOMETRIC_TXN_VALIDITY_SECONDS = 45;
    public final static int BIOMETRIC_MAX_STALE_DAYS = 18;
    public final static String SYS_ADMIN_ROLE = "SYS_ADMIN";
    public final static String DICTATOR_ROLE = "DICTATOR";
}
