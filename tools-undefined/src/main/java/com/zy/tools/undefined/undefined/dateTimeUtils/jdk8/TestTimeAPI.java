package com.zy.tools.undefined.undefined.dateTimeUtils.jdk8;

import org.junit.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;

public class TestTimeAPI {

    /**
     * LocalDate、LocalTime、LocalDateTime
     * LocalDate专门表示日期
     * LocalTime专门表示时间
     * LocalDateTime可以同时表示日期和时间
     *
     */

    // 1.基本年月日,时分秒及当前时间的获取:人所读的
    @Test
    public void fn1(){
        // 1.获取当前时间
        LocalDateTime now = LocalDateTime.now();
        System.out.println("1.获取当前时间=========="+now);
        // 2.设置任意时间
        LocalDateTime ldt = LocalDateTime.of(2018,06,03,12,11,13);
        System.out.println("2.设置任意时间==============="+ldt);
        // 3.增加或减少年月日,时分秒
        LocalDateTime ldt2 = ldt.plusYears(2);
        System.out.println(ldt2);
        ldt.minusMonths(1);
        // 4.获取年月日,时分秒
        System.out.println(ldt.getYear());
        System.out.println(ldt.getMonth());
        System.out.println(ldt.getDayOfMonth());
        System.out.println(ldt.getHour());
        System.out.println(ldt.getMinute());
        System.out.println(ldt.getSecond());
        // 获取毫秒见fn2
        System.out.println(ldt.getNano());
        System.out.println(ldt.getDayOfWeek());
        System.out.println(ldt.getDayOfYear());
    }

    // 2.Instant:时间戳:计算机所读的时间（使用 Unix 元年  1970年1月1日 00:00:00 所经历的毫秒值）
    @Test
    public void fn2(){
        Instant now = Instant.now();
        System.out.println(now);
        OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8));
        System.out.println(offsetDateTime);
        System.out.println(now.toEpochMilli());
        System.out.println(now.getNano());
        Instant instant = Instant.ofEpochSecond(5);
        System.out.println(instant);
    }

    // 3.Duration : 用于计算两个“时间”间隔
    @Test
    public void fn3() throws InterruptedException {
        Instant start = Instant.now();
        Thread.sleep(1000);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Duration==============="+duration.toMillis());
        LocalTime start1 = LocalTime.now();
        Thread.sleep(1000);
        LocalTime end1 = LocalTime.now();
        Duration duration1 = Duration.between(start1, end1);
        System.out.println("Duration==============="+duration1.toMillis());
    }

    // 4.Period : 用于计算两个“日期”间隔
    @Test
    public void fn4() throws InterruptedException {
        LocalDate begin = LocalDate.of(2018, 6, 1);
        LocalDate end = LocalDate.now();
        Period period = Period.between(begin, end);
        System.out.println(period.getYears()+"年"+period.getMonths()+"月"+period.getDays()+"日");
    }

    // 5.TemporalAdjuster : 时间校正器
    @Test
    public void fn5(){
        LocalDateTime now = LocalDateTime.now();
        // 修改至某月
        LocalDateTime ldt2 = now.withMonth(2);
        System.out.println(ldt2);

        // 获取下一个周日的日期
        LocalDateTime ldt3 = now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println(ldt3);

        //自定义：下一个工作日
        LocalDateTime with = now.with((x) -> {
            LocalDateTime localDateTime = (LocalDateTime) x;
            DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
            if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
                return localDateTime.plusDays(3);
            } else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
                return localDateTime.plusDays(2);
            } else {
                return localDateTime.plusDays(1);
            }
        });
        System.out.println(with);
    }

    // 6. DateTimeFormatter : 解析和格式化日期或时间
    @Test
    public void fn6(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String format = dateTimeFormatter.format(now);
        String format1 = now.format(dateTimeFormatter);
        System.out.println(format);
        System.out.println(format1);

        LocalDateTime parse = now.parse(format1, dateTimeFormatter);
        System.out.println(parse);

    }

    // 7.ZonedDate、ZonedTime、ZonedDateTime ： 带时区的时间或日期
    @Test
    public void fn7(){
        Set<String> set = ZoneId.getAvailableZoneIds();
        set.forEach(System.out::println);

    }

    @Test
    public void fn8(){
        LocalDateTime ldt = LocalDateTime.now((ZoneId.of("Asia/Hong_Kong")));
        System.out.println(ldt);
    }

}
