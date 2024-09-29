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
    private final ResultProcessor resultProcessor;
    private final SockDataExceptionChecker sockDataExceptionChecker;
    private final static Lock lock = new ReentrantLock();

    public WencaiConditionCrawler(WencaiConditionMapperExt wencaiConditionMapperExt, TradeDateMapperExt tradeDateMapperExt, WencaiCrawler wencaiCrawler, StockMapperExt stockMapperExt, ConceptMapperExt conceptMapperExt, ResultProcessor resultProcessor, SockDataExceptionChecker sockDataExceptionChecker) {
        this.wencaiConditionMapperExt = wencaiConditionMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
        this.wencaiCrawler = wencaiCrawler;
        this.stockMapperExt = stockMapperExt;
        this.conceptMapperExt = conceptMapperExt;
        this.resultProcessor = resultProcessor;
        this.sockDataExceptionChecker = sockDataExceptionChecker;
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
                    WencaiCondition cookies = wencaiConditionMapperExt.selectByPrimaryKey("cookies");

                    int page = 1;
                    boolean flag = false;
                    log.info("执行数据爬取");
                    for (Map<String, Object> row : wencaiConditions) {
                        int count = 0;
                        flag = true;
                        log.info("爬取数据，query:{}, condition:{}, page:{}, count:{}", row.get("query").toString(), row.get("condition").toString(), page, count);
                        while (flag) {
                            JSONObject result = wencaiCrawler.crawler(row.get("query").toString(), row.get("condition").toString(), cookies.getCondition(), page);
                            List<Stock> rows = resultProcessor.processor(result, row.get("today").toString(), row.get("yesterday").toString());
                            if (rows.size() == 0) {
                                page = 1;
                                break;
                            }
                            count += rows.size();
                            stockMapperExt.saveOrUpdateBatch(rows);
                            int finalCount = count;
                            rows.forEach(i -> {
                                if (!sockDataExceptionChecker.check(i)) {
                                    log.info("写入:{}", i.toString());
                                }
                            });
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
                        log.info("爬取数据完成，总数:{}", count);

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

        String base_query = wencaiConditionMap.get("base_query").getCondition();
        String base_condition = wencaiConditionMap.get("base_condition").getCondition();

        Map row = new HashMap();
        row.put("query", base_query.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row.put("condition", base_condition.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row.put("page", 1);
        row.put("today", today2);
        row.put("yesterday", yesterday2);
        querys.add(row);

        String ceiling_query = wencaiConditionMap.get("ceiling_query").getCondition();
        String ceiling_condition = wencaiConditionMap.get("ceiling_condition").getCondition();
        Map row2 = new HashMap();
        row2.put("query", ceiling_query.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row2.put("condition", ceiling_condition.replaceAll("today1", today1).replaceAll("today2", today2).
                replaceAll("yesterday1", yesterday1).replaceAll("yesterday2", yesterday2).replaceAll("tendays", tendays));
        row2.put("page", 1);
        row2.put("today", today2);
        row2.put("yesterday", yesterday2);
        querys.add(row2);
        return querys;
    }

}
