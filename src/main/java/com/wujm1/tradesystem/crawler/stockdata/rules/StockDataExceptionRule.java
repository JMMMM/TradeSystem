package com.wujm1.tradesystem.crawler.stockdata.rules;

import com.wujm1.tradesystem.entity.Stock;

/**
 * @author wujiaming
 * @date 2024-09-29 17:56
 */
public interface StockDataExceptionRule {

    boolean check(Stock stock);
}
