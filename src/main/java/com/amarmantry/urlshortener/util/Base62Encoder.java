package com.amarmantry.urlshortener.util;

public class Base62Encoder {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String encodeBase62(Long id) {
        StringBuilder result = new StringBuilder();
        while(id > 0) {
            result.append(CHARACTERS.charAt((int) (id % 62)));
            id /= 62;
        }
        return result.reverse().toString();
    }
}
