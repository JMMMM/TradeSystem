package com.wujm1.tradesystem.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wujiaming
 * @date 2024-11-28 15:20
 */
public final class StockCodeFormatter {
    //sz000001
    private static final Pattern p1 = Pattern.compile("(sz|sh)(\\d{6})");

    public static String format(String stockCode) {
        Matcher matcher = p1.matcher(stockCode);
        if (matcher.matches()) {
            return matcher.group(2) + "." + matcher.group(1).toUpperCase(Locale.ROOT);
        }
        return stockCode;
    }
}
