package com.doruk.infrastructure.util;

import com.doruk.infrastructure.config.AppConfig;

public class StringUtil {
    private static final String FLOAT_REGEX = "^[-+]?[0-9]*\\\\.?[0-9]+$";

    public static String generateUrl(AppConfig config, String route) {
        return config.appUrl() + "/users/verify/email" + route;
    }

    public static boolean isNumber(String value) {
        return value.matches(FLOAT_REGEX);
    }
}
