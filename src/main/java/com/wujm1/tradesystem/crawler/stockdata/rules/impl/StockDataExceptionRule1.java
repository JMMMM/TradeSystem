package com.wujm1.tradesystem.crawler.stockdata.rules.impl;

import com.alibaba.druid.util.StringUtils;
import com.wujm1.tradesystem.crawler.stockdata.rules.StockDataExceptionRule;
import com.wujm1.tradesystem.entity.Stock;
import org.springframework.stereotype.Component;

/**
 * @author wujiaming
 * @date 2024-09-29 17:57
 * 涨停基础数据校验
 */
@Component
public class StockDataExceptionRule1 implements StockDataExceptionRule {

    @Override
    public boolean check(Stock stock) {
        //是否涨停;
        Boolean isCeiling = stock.getCeilingDays() > 0;

        if (isCeiling) {
            //涨停时间不全
            if (StringUtils.isEmpty(stock.getFirstCeilingTime()) || StringUtils.isEmpty(stock.getLastCeilingTime())) {
                return false;
            }
            //没有涨停原因
            if (StringUtils.isEmpty(stock.getReason())) {
                return false;
            }
        }
        return true;
    }
}
