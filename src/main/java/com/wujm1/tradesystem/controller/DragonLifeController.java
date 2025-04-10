package com.wujm1.tradesystem.controller;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Maps;
import com.wujm1.tradesystem.constants.StockConstants;
import com.wujm1.tradesystem.entity.Emotion;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.ext.EmotionMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wujiaming
 * @date 2024-12-06 10:07
 */
@RequestMapping("/dragonlife")
@RestController
@Slf4j
public class DragonLifeController {
    private final StockMapperExt stockMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;
    private final EmotionMapperExt emotionMapperExt;

    public DragonLifeController(StockMapperExt stockMapperExt, TradeDateMapperExt tradeDateMapperExt, EmotionMapperExt emotionMapperExt) {
        this.stockMapperExt = stockMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
        this.emotionMapperExt = emotionMapperExt;
    }

    @GetMapping("/show")
    public Object show(@RequestParam(value = "startDate", defaultValue = "20220101") String startDate,
                       @RequestParam(value = "endDate", defaultValue = "20241231") String endDate) throws IOException {

        Date now = new Date();
        List<String> tradeDates = tradeDateMapperExt.showAllTradeDate(startDate, DateUtils.getDateStr(now, "yyyyMMdd")).stream().map(TradeDate::getDate).collect(Collectors.toList());

        List<Stock> dragons = stockMapperExt.queryDragonStock(startDate, endDate).stream().filter(i -> !StringUtils.isEmpty(i.getRemark())).collect(Collectors.toList());

        //1.创建一个工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建一个表
        Sheet sheet = workbook.createSheet("sheet1");
        int row_num = 0;
        Row row = sheet.createRow(row_num);

        //yellow 加速
        CellStyle yellow = workbook.createCellStyle();
        yellow.setFillForegroundColor(IndexedColors.YELLOW.index);
        yellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //退潮
        CellStyle green = workbook.createCellStyle();
        green.setFillForegroundColor(IndexedColors.GREEN.index);
        green.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //结束
        CellStyle blue = workbook.createCellStyle();
        blue.setFillForegroundColor(IndexedColors.BLUE.index);
        blue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //第一行日期
        Map<String, Integer> dateMap = Maps.newHashMap();
        for (int i = 0; i < tradeDates.size(); i++) {
            String date = tradeDates.get(i);
            Cell cell = row.createCell(i);
            cell.setCellValue(date);
            dateMap.put(date, i);
        }

        row_num = 0;
        Row row2;
        Map<String, Row> rowMap = Maps.newHashMap();
        Map<String, Integer> ceilingCounting = Maps.newHashMap();
        Map<String, Stock> lastStockMap = Maps.newHashMap();
        for (Stock dragon : dragons) {
            String name = dragon.getName();
            String date = dragon.getDate();
            Integer ceiling = dragon.getCeilingDays();
            int index = dateMap.get(date);
            if (rowMap.get(name) == null) {
                row_num++;
                row2 = sheet.createRow(row_num);
            } else {
                row2 = rowMap.get(name);
            }

            Cell cell = row2.createCell(index);
            if (dragon.getRemark().equals(StockConstants.UPING)) {
                if (ceiling > 0) {
                    if (lastStockMap.containsKey(name) && (lastStockMap.get(name).getHigh().compareTo(dragon.getHigh()) > 0 || tradeDates.indexOf(dragon.getDate()) - tradeDates.indexOf(lastStockMap.get(name).getDate()) >= 3)) {
                        //不能反包就重置
                        ceilingCounting.put(name, 1);
                    } else {
                        Integer last = ceilingCounting.getOrDefault(name, 0);
                        ceilingCounting.put(name, last + 1);
                    }
                }
                cell.setCellStyle(yellow);
            } else if (dragon.getRemark().equals(StockConstants.DOWNING)) {
                ceilingCounting.put(name, 0);
                cell.setCellStyle(green);
            } else if (dragon.getRemark().equals(StockConstants.ENDING)) {
                cell.setCellStyle(blue);
            } else if (dragon.getRemark().equals(StockConstants.PD)) {
                ceilingCounting.put(name, 0);
            }
            if (ceilingCounting.getOrDefault(name, 0) == 0) {
                cell.setCellValue(name);
            } else {
                cell.setCellValue(name + "(" + ceilingCounting.get(name) + ")");
            }
            rowMap.put(name, row2);
            lastStockMap.put(name, dragon);
        }

        // 保存Excel文件
        FileOutputStream fileOut = new FileOutputStream("./dragonlife.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // 关闭工作簿
        workbook.close();
        return dragons;
    }

}
