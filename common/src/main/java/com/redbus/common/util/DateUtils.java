package com.redbus.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    
    private DateUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
    
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_FORMAT);
    }
    
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }
    
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    public static long toEpochMilli(LocalDateTime dateTime) {
        if (dateTime == null) return 0;
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
    
    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);
    }
    
    public static boolean isBeforeNow(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isBefore(LocalDateTime.now());
    }
    
    public static boolean isAfterNow(LocalDateTime dateTime) {
        return dateTime != null && dateTime.isAfter(LocalDateTime.now());
    }
    
    public static long getDaysBetween(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0;
        return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
    }
    
    public static long getMinutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0;
        return Duration.between(start, end).toMinutes();
    }
}
