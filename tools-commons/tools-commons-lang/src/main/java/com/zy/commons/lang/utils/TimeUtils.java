package com.zy.commons.lang.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeUtils {
    private TimeUtils() {
        throw new RuntimeException("TimeUtils can not instantiated.");
    }

    public static final String TIME_FORMAT_01 = "yyyy-MM-dd HH:mm:ss";

    public static long parseEpochStr2Mills(String epochStr, String format) {
        return LocalDateTime.from(DateTimeFormatter.ofPattern(format).parse(epochStr)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static String parseMillsTimestamp2Str(long mills, String format) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern(format).format(localDateTime);
    }

}
