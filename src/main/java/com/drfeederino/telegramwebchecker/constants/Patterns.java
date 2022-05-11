package com.drfeederino.telegramwebchecker.constants;

import java.util.regex.Pattern;

public final class Patterns {
    public static final Pattern NUMBER = Pattern.compile("(?<!\\d)\\d{9}(?!\\d)");
    public static final Pattern BARCODE_NUMBER = Pattern.compile("(?<!\\d)\\d{7}(?!\\d)");
    private Patterns() {
    }

}
