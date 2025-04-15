package com.wujm1.tradesystem.utils;

import com.wujm1.tradesystem.entity.TableData;

import java.util.List;

public final class TableBuilder {
    private static final String HEADER = "%-18s|%-12s|%-4s|%-16s|%-16s|%-12s|%-100s";  // 列格式规范
    private static final String SEPARATOR = "------------------+------------+----+----------------+----------------+------------+----------------------------------------------------------------------------------------------------";

    public static String buildTable(List<TableData> dataList) {
        StringBuilder sb = new StringBuilder();

        // 添加表头
        sb.append(String.format(HEADER, "code", "name", "k", "start", "end", "涨幅", "max(concepts)")).append("</br>");

        // 添加分隔线
        sb.append(SEPARATOR).append("</br>");

        // 添加数据行
        dataList.forEach(data -> {
            String concepts = String.join(";", data.getConcepts());
            // 数据行处理示例
            String name = padRight(data.getName().replaceAll("([\u4e00-\u9fa5]{4})", "$1　"), 12);
            sb.append(String.format(HEADER, data.getCode(), name, data.getK(), data.getStartDate(), data.getEndDate(), String.format("%.2f", data.getIncreaseRate()), concepts)).append("</br>");
        });

        return sb.toString();
    }
    // 修改后的字符对齐方法（支持中英文混排）
    private static String padRight(String str, int targetWidth) {
        int displayWidth = getDisplayWidth(str);
        if (displayWidth >= targetWidth) return str;

        // 计算需要填充的全角空格数量（每个全角空格占2字符宽度）
        int padCount = (targetWidth - displayWidth) / 2;
        return str + String.format("%-" + padCount + "s", "").replace(' ', '　');
    }

    // 精确计算显示宽度
    private static int getDisplayWidth(String s) {
        return s.codePoints().map(c -> (c >= 0x4E00 && c <= 0x9FA5) ? 2 : 1).sum();
    }

}