package com.wujm1.tradesystem.controller;

import cn.hutool.core.lang.Pair;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.service.CybPremiumCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @autest wujiaming
 * @date 2024-09-23 16:52
 */
@RequestMapping("/cyb")
@RestController
@Slf4j
public class CybChanceController {

    @Autowired
    private CybPremiumCalculator cybPremiumCalculator;
    @Autowired
    private TradeDateMapperExt tradeDateMapperExt;

    @GetMapping("/premium")
    public Object premium(@RequestParam("date") String date, @RequestParam("range") Integer range) {
        List<TradeDate> tradeDateList = tradeDateMapperExt.selectByDateTopN(date, range);
        List<Pair<String, BigDecimal>> result = Lists.newArrayList();
        for (TradeDate tradeDate : tradeDateList) {
            result.add(cybPremiumCalculator.firstCeilingPremium(tradeDate.getDate()));
        }
        log.info("截至到{},近{}日，平均溢价率:{}", date, range, result.stream().map(Pair::getValue).reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(result.size()), 2, BigDecimal.ROUND_HALF_UP));
        return result;
    }
}
