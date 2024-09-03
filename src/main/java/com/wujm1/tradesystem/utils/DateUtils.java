package com.wujm1.tradesystem.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author wujiaming
 * @date 2024-09-02 17:02
 */
public final class DateUtils {
    
    /**
     * @param date   日期
     * @param format yyyymmdd
     * @return
     */
    public static String getDateStr(Date date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return localDateTime.format(formatter);
    }
    
    public static Date getYear(Date date, int minusyear) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        localDateTime.minusYears(minusyear);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public static Date parseLocalDate(String date_str, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDate localDate = LocalDate.parse(date_str, formatter);
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String changeDateFormat(String date_str,String origin_format,String target_format){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(origin_format);
        LocalDate localDate = LocalDate.parse(date_str, formatter);
        return localDate.format(DateTimeFormatter.ofPattern(target_format));
    }
    
}
