package com.doruk.infrastructure.util;

import com.doruk.application.enums.ImageVariant;

public final class ImageVariantKey {

    private ImageVariantKey() {}

    public static String of(String originalKey, ImageVariant variant) {
        int dot = originalKey.lastIndexOf('.');
        if (dot == -1) {
            return originalKey + "@" + variant.suffix();
        }
        if (variant == ImageVariant.ORIGINAL)
            return originalKey;

        return originalKey.substring(0, dot)
                + "@" + variant.suffix()
                + originalKey.substring(dot);
    }
}