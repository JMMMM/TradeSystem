package com.wujm1.tradesystem.crawler.tradedate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.constants.YesOrNoEnum;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Desctiption <Template>
 * @Author wujiaming
 * @Date 2024/9/3
 */
@Component
@Slf4j
public class TradeDateCrawler {

    @Value("${holiday.url}")
    private String YEAR_HOLIDAY_URL;

    private final RestTemplate crawlerRestTemplate;

    private final TradeDateMapperExt tradeDateMapperExt;


    public TradeDateCrawler(RestTemplate crawlerRestTemplate, TradeDateMapperExt tradeDateMapperExt) {
        this.crawlerRestTemplate = crawlerRestTemplate;
        this.tradeDateMapperExt = tradeDateMapperExt;
    }

    public void initTradeDate(String year) {
        String response = crawlerRestTemplate.getForEntity(String.format(YEAR_HOLIDAY_URL, year), String.class).getBody();
        List<String> tradeDates = process(JSONObject.parseObject(response, HashMap.class));
        //去掉周六周日
        List<String> result = tradeDates.stream().filter(i -> {
            Date date = DateUtils.parseLocalDate(i, "yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return !(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(result)) {
            log.info("没有获取到交易日,{}", response);
            return;
        }
        List<TradeDate> last_result = result.stream().map(this::convertToTradeDate).collect(Collectors.toList());
        tradeDateMapperExt.saveOrUpdateBatch(last_result);
    }

    private static List<String> process(Map<String, Object> datas) {
        String code = String.valueOf(datas.get("code"));
        String msg = String.valueOf(datas.get("msg"));
        List<String> result = Lists.newArrayList();
        if (code.equals("0") && msg.equals("ok")) {
            Map data = (Map) datas.get("data");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
            for (Map<String, Object> d : list) {
                result.add(d.get("date").toString());
            }
        }
        return result;
    }

    public TradeDate convertToTradeDate(String date) {
        TradeDate holiday = new TradeDate();
        holiday.setDate(date);
        holiday.setYear(date.substring(0, 4));
        holiday.setMonth(date.substring(4, 6));
        holiday.setDay(date.substring(6, 8));
        holiday.setTradeDate(YesOrNoEnum.YES.getCode());
        return holiday;
    }

}
