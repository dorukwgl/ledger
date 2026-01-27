package com.doruk.infrastructure.util;

import com.doruk.infrastructure.config.AppConfig;

public class StringUtil {
    public static String generateUrl(AppConfig config, String route) {
        return config.appUrl() + "/users/verify/email" + route;
    }
}
