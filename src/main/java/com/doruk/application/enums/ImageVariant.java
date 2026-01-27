package com.doruk.application.enums;

public enum ImageVariant {
    PICO("pc", 64),
    SMALL("sm", 320),
    MEDIUM("md", 640),
    ORIGINAL("", -1);

    private final String suffix;
    private final int maxSize;

    ImageVariant(String suffix, int maxSize) {
        this.suffix = suffix;
        this.maxSize = maxSize;
    }

    public String suffix() {
        return suffix;
    }

    public int maxSize() {
        return maxSize;
    }
}
