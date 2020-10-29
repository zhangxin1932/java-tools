package com.zy.tools.undefined.dateTimeUtils.threadLocal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间格式化工具类一
 */
public class DateFormatTools {

    // 此格式将会返回带有时区的时间格式
    private static final ThreadLocal<DateFormat> dateFormatThreadLocal =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));

    /**
     * 格式化时间格式
     * @param date
     * @return
     */
    public static String convertDateToString(Date date) {
        return date == null ? null : dateFormatThreadLocal.get().format(date);
    }


}
