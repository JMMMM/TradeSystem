package com.wujm1.tradesystem.crawler.stockdata.rules.impl;

import com.wujm1.tradesystem.crawler.stockdata.rules.StockDataExceptionRule;
import com.wujm1.tradesystem.entity.Stock;
import org.springframework.stereotype.Component;

/**
 * @author wujiaming
 * @date 2024-09-29 18:07
 * stock 基础数据校验
 */
@Component
public class StockDataExceptionRule2 implements StockDataExceptionRule {

    @Override
    public boolean check(Stock stock) {
        //K线数据要完整
        return true;
    }
}
