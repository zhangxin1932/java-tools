package com.zy.tools.undefined.spring.custom.starter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateParse {

    private final String format;

    public DateParse(String format) {
        this.format = format;
    }

    public String parseLocalDateTime2Str(LocalDateTime time) {
        return Objects.isNull(time) ? null : DateTimeFormatter.ofPattern(format).format(time);
    }
}
