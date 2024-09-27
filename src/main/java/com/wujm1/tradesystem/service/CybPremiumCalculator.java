package com.wujm1.tradesystem.service;

import cn.hutool.core.lang.Pair;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wujiaming
 * @date 2024-09-23 16:27
 * @description 创业板溢价率计算器
 */
@Service
@Slf4j
public class CybPremiumCalculator {

    private final StockMapperExt stockMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;

    public CybPremiumCalculator(StockMapperExt stockMapperExt, TradeDateMapperExt tradeDateMapperExt) {
        this.stockMapperExt = stockMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
    }

    /**
     * 计算最今天的创业板溢价率
     */
    public Pair<String, BigDecimal> firstCeilingPremium(String date) {
        //获取近2个交易日
        List<TradeDate> tradeDates = tradeDateMapperExt.selectByDateTopN(date, 2);
        //获取昨日创业板涨停股票
        List<Stock> y_stocks = stockMapperExt.queryCybCeilingStock(tradeDates.get(0).getDate(), 1);
        //计算今日溢价
        List<Stock> t_stocks = stockMapperExt.queryStockByCodes(y_stocks.stream().map(Stock::getCode).collect(Collectors.toList()), date);
        Map<String, BigDecimal> permiumMap = t_stocks.stream().collect(Collectors.toMap(Stock::getCode,
                stock -> (stock.getOpen().subtract(stock.getrClose())).divide(stock.getrClose(), 2)));

        BigDecimal avg = permiumMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(permiumMap.size()), 2, BigDecimal.ROUND_HALF_UP);

        log.info("{}创业板溢价率：{}", date, avg);
        Pair<String, BigDecimal> pair = new Pair<>(date, avg);
        return pair;
    }

}
