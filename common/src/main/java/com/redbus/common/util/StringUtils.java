package com.redbus.common.util;

import java.util.Random;
import java.util.UUID;

public class StringUtils {
    
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    
    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    public static String generatePNR() {
        return generateRandomAlphanumeric(10);
    }
    
    public static String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + generateRandomAlphanumeric(6);
    }
    
    public static String generateBookingId() {
        return "BKG" + System.currentTimeMillis() + generateRandomAlphanumeric(6);
    }
    
    public static String generateRandomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }
    
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    public static String trimToNull(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
    
    public static String truncate(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }
    
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    public static String toSnakeCase(String str) {
        if (isEmpty(str)) return str;
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
    
    public static String toCamelCase(String str) {
        if (isEmpty(str)) return str;
        String[] parts = str.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(capitalize(parts[i]));
        }
        return camelCase.toString();
    }
}
