package com.wujm1.tradesystem.controller;

import com.alibaba.druid.util.StringUtils;
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
 * @date 2024-02-21 11:01
 * 周期
 */
@RestController
@RequestMapping("/node")
@Slf4j
public class NodeController {

    private final StockMapperExt stockMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;
    private final EmotionMapperExt emotionMapperExt;

    public NodeController(StockMapperExt stockMapperExt, TradeDateMapperExt tradeDateMapperExt, EmotionMapperExt emotionMapperExt) {
        this.stockMapperExt = stockMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
        this.emotionMapperExt = emotionMapperExt;
    }

    /**
     * 情绪周期
     *
     * @param startDate
     * @param min_ceilingDays
     * @return
     * @throws IOException
     */
    @GetMapping("/show")
    public Object show(@RequestParam(value = "startDate", defaultValue = "20220101") String startDate,
                       @RequestParam(value = "ceilingdays", defaultValue = "3") int min_ceilingDays) throws IOException {

        Date now = new Date();
        List<String> tradeDates = tradeDateMapperExt.showAllTradeDate(startDate, DateUtils.getDateStr(now, "yyyyMMdd")).stream().map(TradeDate::getDate).collect(Collectors.toList());

        List<Stock> dragons = stockMapperExt.queryCeilingDays(startDate, min_ceilingDays);

        List<List<Stock>> result = new ArrayList<>();

        List<Emotion> emotions = emotionMapperExt.emotionQuery(startDate);

        Map<String, Emotion> emotionsMap =
                emotions.stream().collect(Collectors.toMap(i -> i.getDate(), Function.identity()));


        for (int i = 0; i < dragons.size(); i++) {
            List<Stock> row = new ArrayList<>();
            Stream.iterate(0, t -> t + 1).limit(tradeDates.size()).forEach(k -> row.add(new Stock()));
            Stock dragon = dragons.get(i);
            String end = dragon.getDate();
            String code = dragon.getCode();
            int ceilingDays = dragon.getCeilingDays();
            int to = tradeDates.indexOf(end);
            int from = to - ceilingDays + 1;
            if (from < 0) {
                continue;
            }
            String start = tradeDates.get(from);

            List<Stock> lives = stockMapperExt.queryStockByDates(code, start, end);
            for (int j = 0; j < lives.size(); j++) {
                row.add(from + j, lives.get(j));
            }
            result.add(row);
        }
        result = result.stream().sorted((a, b) -> {
            Integer left =
                    Integer.parseInt(a.stream().filter(Objects::nonNull).findFirst().map(Stock::getDate).orElse("0"));
            Integer right = Integer.parseInt(b.stream().filter(Objects::nonNull).findFirst().map(Stock::getDate).orElse("0"));
            return left.compareTo(right);
        }).collect(Collectors.toList());


        //1.创建一个工作簿
        Workbook workbook = new XSSFWorkbook();
        //2.创建一个表
        Sheet sheet = workbook.createSheet("sheet1");
        //yellow 加速
        CellStyle yellow = workbook.createCellStyle();
        yellow.setFillForegroundColor(IndexedColors.YELLOW.index);
        yellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle blue = workbook.createCellStyle();
        //分歧
        blue.setFillForegroundColor(IndexedColors.BLUE.index);
        blue.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle green = workbook.createCellStyle();


        green.setFillForegroundColor(IndexedColors.GREEN.index);
        green.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        int row_num = 0;
        Row row = sheet.createRow(row_num);

        for (int i = 0; i < tradeDates.size(); i++) {
            String date = tradeDates.get(i);
            Emotion emotion = emotionsMap.get(date);
            if (emotion == null) {
                break;
            }
            Cell cell = row.createCell(i);

            BigDecimal emotion_close = emotion.getClose();
            BigDecimal emotion_ma3 = emotion.getMa3();
            BigDecimal chg = emotion.getChg();

            //冰点不回踩3日线
            if (chg.compareTo(new BigDecimal("1.00")) < 0 && emotion_close.compareTo(emotion_ma3) > 0) {
                cell.setCellStyle(green);
            }

            if (chg.compareTo(new BigDecimal("1.00")) < 0 && emotion_close.compareTo(emotion_ma3) <= 0) {
                cell.setCellStyle(blue);
            }
            if (chg.compareTo(new BigDecimal("5.0")) >= 0) {
                cell.setCellStyle(yellow);
            }

            cell.setCellValue(date + "(" + chg.toPlainString() + ")");
        }

        row_num = 2;
        for (int i = 0; i < result.size(); i++) {
            List<Stock> lives = result.get(i);
            row = sheet.createRow(row_num + i);
            Stock maxVolStock = lives.get(0);
            for (int col = 0; col < lives.size(); col++) {
                Stock stock = lives.get(col);
                if (Objects.isNull(stock) || StringUtils.isEmpty(stock.getName())) {
                    continue;
                }
                Cell cell = row.createCell(col);
                maxVolStock = getBiggerVolStock(maxVolStock, stock);
                if (Objects.equals(maxVolStock, stock)) {
                    cell.setCellStyle(blue);
                } else {
                    cell.setCellStyle(yellow);
                }
                cell.setCellValue(stock.getName() + stock.getCeilingDays());
            }
        }

        row_num = 1;
        Row row1 = sheet.createRow(row_num);
        for (int i = 0; i < tradeDates.size(); i++) {
            final int k = i;
            Cell cell = row1.createCell(i);
            int max_ceiling_days = result.stream().map(t -> t.get(k)).max((a, b) -> {
                int left = Objects.isNull(a.getCeilingDays()) ? 0 : a.getCeilingDays();
                int right = Objects.isNull(b.getCeilingDays()) ? 0 : b.getCeilingDays();
                return left - right;
            }).map(Stock::getCeilingDays).orElse(0);
            cell.setCellValue(max_ceiling_days);
        }

        // 保存Excel文件
        FileOutputStream fileOut = new FileOutputStream("./example.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // 关闭工作簿
        workbook.close();
        return result;
    }

    public Boolean isOne(Stock stock) {
        return Objects.equals(stock.getLow(), stock.getHigh());
    }


    public Stock getBiggerVolStock(Stock left, Stock right) {
        if (Objects.isNull(left) || Objects.isNull(left.getVol())) {
            return right;
        }
        if (Objects.isNull(right) || Objects.isNull(right.getVol())) {
            return left;
        }
        if (left.getVol().compareTo(right.getVol()) > 0) {
            return left;
        }
        return right;
    }
}
