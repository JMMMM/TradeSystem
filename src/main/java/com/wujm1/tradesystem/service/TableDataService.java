package com.wujm1.tradesystem.service;

import com.google.common.collect.Lists;
import com.wujm1.tradesystem.constants.StockConstants;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.TableData;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.ext.ConceptMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.utils.TableBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableDataService {

    private final TradeDateMapperExt tradeDateMapperExt;
    private final StockMapperExt stockMapperExt;
    private final ConceptMapperExt conceptMapperExt;

    public TableDataService(TradeDateMapperExt tradeDateMapperExt, StockMapperExt stockMapperExt, ConceptMapperExt conceptMapperExt) {
        this.tradeDateMapperExt = tradeDateMapperExt;
        this.stockMapperExt = stockMapperExt;
        this.conceptMapperExt = conceptMapperExt;
    }

    public String zhouqi(String start, String end) {
        List<TradeDate> tradeDates = tradeDateMapperExt.showAllTradeDate(start, end);
        int index = 0;
        String begin1 = tradeDates.get(index).getDate();
        index++;
        String begin2 = tradeDates.get(index).getDate();
        List<Stock> begin = begin(begin1, begin2);
        List<TableData> base = begin.stream().map(this::converToTableData).collect(Collectors.toList());

        for (; index < tradeDates.size(); index++) {
            base = nextDate(base, tradeDates.get(index).getDate());
        }
        for (TableData tableData : base) {
            tableData.setIncreaseRate(tableData.getMaxStock().getClose().divide(tableData.getMinStock().getrClose(), 2));
        }

        base = base.stream().sorted(Comparator.comparing(TableData::getIncreaseRate).reversed()).collect(Collectors.toList());

        return TableBuilder.buildTable(base);

    }

    private TableData converToTableData(Stock stock) {
        TableData tableData = new TableData();
        tableData.setCode(stock.getCode());
        tableData.setName(stock.getName());
        tableData.setK(stock.getCeilingDays());
        tableData.setStartDate(stock.getDate());
        tableData.setConcepts(Arrays.asList(stock.getConcepts().split(",")));
        tableData.setEndDate(stock.getDate());
        tableData.setMinLow(stock.getLow());
        tableData.setMaxHigh(stock.getHigh());
        tableData.setMinStock(stock);
        tableData.setMaxStock(stock);
        return tableData;
    }

    public List<TableData> nextDate(List<TableData> tableDatas, String date) {
        List<String> stockCodes = tableDatas.stream().filter(i -> i.getCnt() < 3).map(TableData::getCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(stockCodes)) {
            return tableDatas;
        }
        List<Stock> stocks = stockMapperExt.queryStockByCodes(stockCodes, date);
        Map<String, Stock> stockMap = stocks.stream().collect(Collectors.toMap(Stock::getCode, stock -> stock));
        for (TableData tableData : tableDatas) {
            Stock stock = stockMap.get(tableData.getCode());
            if (Objects.isNull(stock) || stock.getDate().equals(tableData.getEndDate())) {
                continue;
            }
            //今日最高价大于昨日收盘价，那么就判断为未结束
            if (stock.getHigh().compareTo(tableData.getMaxStock().getHigh()) > 0) {
                tableData.setEndDate(stock.getDate());
                tableData.setCnt(1);
                tableData.setMaxStock(stock);
                tableData.setMinLow(stock.getLow());
                tableData.setMaxHigh(stock.getHigh());

                if (stock.getCeilingDays() > 0) {
                    tableData.setK(tableData.getK() + 1);
                }
            } else if (isWrapper(stock, tableData.getMaxStock())) {
                //无效k线要到下一天确定,但以包裹那根K线为准
                tableData.setEndDate(stock.getDate());
                tableData.setMinLow(stock.getLow().compareTo(tableData.getMinLow()) < 0 ? stock.getLow() : tableData.getMinLow());
                tableData.setMaxHigh(stock.getHigh().compareTo(tableData.getMaxHigh()) > 0 ? stock.getHigh() : tableData.getMaxHigh());
            } else {
                tableData.setCnt(tableData.getCnt() + 1);
            }
        }
        return tableDatas;
    }


    //节点票
    public List<Stock> begin(String begin1, String begin2) {
        List<Stock> result = Lists.newArrayList();
        List<Stock> stock1 = stockMapperExt.queryStockByTime(begin1, begin1);
        stock1 = stock1.stream().filter(i -> Optional.ofNullable(i.getCeilingDays()).orElse(0) == 1).collect(Collectors.toList());
        result.addAll(stock1);
        List<Stock> stock2 = stockMapperExt.queryStockByTime(begin2, begin2);
        List<String> stock1Codes = stock1.stream().map(Stock::getCode).collect(Collectors.toList());
        stock2 = stock2.stream().filter(i -> Optional.ofNullable(i.getCeilingDays()).orElse(0) == 1 && !stock1Codes.contains(i.getCode())).collect(Collectors.toList());
        result.addAll(stock2);
        return result;
    }

    private Boolean isWrapper(Stock stock, Stock lastStock) {
        return lastStock.getHigh().compareTo(stock.getHigh()) >= 0 && lastStock.getLow().compareTo(stock.getLow()) <= 0;
    }
}
