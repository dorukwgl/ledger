package com.doruk.infrastructure.util;

import com.doruk.application.enums.ImageVariant;

import java.util.ArrayList;
import java.util.List;

public final class VariantKeysUtil {

    private VariantKeysUtil() {}

    public static List<String> keysOf(String originalKey) {
        List<String> keys = new ArrayList<>();
        keys.add(originalKey);

        for (ImageVariant variant : ImageVariant.values()) {
            keys.add(ImageVariantKey.of(originalKey, variant));
        }
        return keys;
    }
}
