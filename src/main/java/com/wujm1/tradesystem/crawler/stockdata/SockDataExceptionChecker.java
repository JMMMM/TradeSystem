package com.wujm1.tradesystem.crawler.stockdata;

import com.google.common.collect.Lists;
import com.wujm1.tradesystem.crawler.stockdata.rules.StockDataExceptionRule;
import com.wujm1.tradesystem.entity.Stock;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author wujiaming
 * @date 2024-09-29 17:56
 */
@Component
public class SockDataExceptionChecker {

    private final List<StockDataExceptionRule> rules;

    public SockDataExceptionChecker(List<StockDataExceptionRule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    }

    public boolean check(Stock stock) {

        for (StockDataExceptionRule rule : rules) {
            if (!rule.check(stock)) {
                return false;
            }
        }

        return true;
    }
}
