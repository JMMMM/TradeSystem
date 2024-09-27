package com.wujm1.tradesystem.crawler.stockdata;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.Concept;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.ext.ConceptMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wujiaming
 * @date 2024-09-03 9:06
 */
@Component
@Slf4j
public class WencaiConditionCrawler {
    private final WencaiConditionMapperExt wencaiConditionMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;
    private final WencaiCrawler wencaiCrawler;
    private final StockMapperExt stockMapperExt;
    private final ConceptMapperExt conceptMapperExt;
    private final static Lock lock = new ReentrantLock();

    public WencaiConditionCrawler(WencaiConditionMapperExt wencaiConditionMapperExt, TradeDateMapperExt tradeDateMapperExt, WencaiCrawler wencaiCrawler, StockMapperExt stockMapperExt, ConceptMapperExt conceptMapperExt) {
        this.wencaiConditionMapperExt = wencaiConditionMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
        this.wencaiCrawler = wencaiCrawler;
        this.stockMapperExt = stockMapperExt;
        this.conceptMapperExt = conceptMapperExt;
    }

    public void initSearchCondition(String date) {
        try {
            if (lock.tryLock(800, TimeUnit.MILLISECONDS)) {
                try {
                    //获取最近两个交易日
                    List<TradeDate> tradeDateLast10 = tradeDateMapperExt.selectByDateTopN(date, 10);
                    List<String> dates = tradeDateLast10.stream().map(TradeDate::getDate).collect(Collectors.toList());
                    if (!dates.contains(date)) {
                        log.info("当日不是交易日，不拉取数据");
                        return;
                    }

                    log.info("拉取最近两个交易日数据：{},{},{}", tradeDateLast10.get(tradeDateLast10.size() - 1).getDate(), tradeDateLast10.get(tradeDateLast10.size() - 2).getDate(), tradeDateLast10.get(0).getDate());
                    List<Map<String, Object>> wencaiConditions = replaceCondition(tradeDateLast10.get(tradeDateLast10.size() - 1).getDate(), tradeDateLast10.get(tradeDateLast10.size() - 2).getDate(), tradeDateLast10.get(0).getDate());
                    for (Map<String, Object> row : wencaiConditions) {
                        log.info("构建wencai搜索语句:{}", JSONObject.toJSONString(row));
                    }
                    WencaiCondition cookies = wencaiConditionMapperExt.selectByPrimaryKey("cookies");
                    ResultProcessor processor = new ResultProcessor();

                    int page = 1;
                    boolean flag = false;
                    int count = 0;
                    log.info("执行数据爬取");
                    for (Map<String, Object> row : wencaiConditions) {
                        flag = true;
                        while (flag) {
                            JSONObject result = wencaiCrawler.crawler(row.get("query").toString(), row.get("condition").toString(), cookies.getCondition(), page);
                            List<Stock> rows = processor.processor(result, row.get("today").toString(), row.get("yesterday").toString());
                            if (rows.size() == 0) {
                                page = 1;
                                break;
                            }
                            count += rows.size();
                            stockMapperExt.saveOrUpdateBatch(rows);
                            int finalCount = count;
                            rows.forEach(i -> log.info("共写入{}个:{}", finalCount, i.toString()));
                            List<Concept> concepts = rows.stream().map(i -> {
                                String[] concept = i.getConcepts().split(";");
                                return Arrays.asList(concept).stream().map(j -> {
                                    Concept concept1 = new Concept();
                                    concept1.setCode(i.getCode());
                                    concept1.setConcept(j);
                                    return concept1;
                                });
                            }).flatMap(i -> i).collect(Collectors.toList());
                            conceptMapperExt.saveOrUpdateBatch(concepts);

                            if (rows.size() < 100) {
                                page = 1;
                                break;
                            } else {
                                page++;
                            }
                        }
                    }
                    log.info("数据爬取完成");
                } finally {
                    lock.unlock();
                }
            } else {
                log.error("程序正在执行");
            }
        } catch (InterruptedException e) {
            log.error("程序正在执行,{}", e.getMessage(), e);
        }

    }


    private List<Map<String, Object>> replaceCondition(String today, String yesterday, String tendays) {
        String today1 = DateUtils.changeDateFormat(today, "yyyyMMdd", "yyyy年MM月dd日");
        String today2 = today;

        String yesterday1 = DateUtils.changeDateFormat(yesterday, "yyyyMMdd", "yyyy年MM月dd日");
        String yesterday2 = yesterday;

        Map<String, WencaiCondition> wencaiConditionMap = wencaiConditionMapperExt.selectAll().stream()
                .collect(Collectors.toMap(WencaiCondition::getConditionName, Function.identity()));

        List<Map<String, Object>> querys = Lists.newArrayList();

        String query_not = wencaiConditionMap.get("query_not").getCondition();
        String condition_not = wencaiConditionMap.get("condition_not").getCondition();

        Map row = new HashMap();
        row.put("query", query_not.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row.put("condition", condition_not.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row.put("page", 1);
        row.put("today", today2);
        row.put("yesterday", yesterday2);
        querys.add(row);

        String query = wencaiConditionMap.get("query").getCondition();
        String condition = wencaiConditionMap.get("condition").getCondition();
        Map row2 = new HashMap();
        row2.put("query", query.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row2.put("condition", condition.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row2.put("page", 1);
        row2.put("today", today2);
        row2.put("yesterday", yesterday2);
        querys.add(row2);

        String query_all = wencaiConditionMap.get("query_all").getCondition();
        String condition_all = wencaiConditionMap.get("condition_all").getCondition();
        Map row3 = new HashMap();
        row3.put("query", query_all.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row3.put("condition", condition_all.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row3.put("page", 1);
        row3.put("today", today2);
        row3.put("yesterday", yesterday2);
        querys.add(row3);
        return querys;
    }

}
