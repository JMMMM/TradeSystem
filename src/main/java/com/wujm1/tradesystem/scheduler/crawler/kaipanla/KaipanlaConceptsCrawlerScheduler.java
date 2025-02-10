package com.wujm1.tradesystem.scheduler.crawler.kaipanla;

import com.wujm1.tradesystem.crawler.jiuyangongshe.JiuyangongsheCrawler;
import com.wujm1.tradesystem.crawler.kpl.KaipanlaConceptsCrawler;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author wujiaming
 * @date 2025-02-10 18:47
 */
@Component
@Slf4j
public class KaipanlaConceptsCrawlerScheduler {
    
    @Autowired
    private KaipanlaConceptsCrawler kaipanlaConceptsCrawler;
    
    @Autowired
    private StockMapperExt stockMapperExt;
    
    @Scheduled(cron = "0 30 23 * * ?")
    public void run() {
        String today = DateUtils.getDateStr(new Date(), "yyyyMMdd");
        log.info("爬取开盘啦概念开始");
        kaipanlaConceptsCrawler.initKaipanlaConcepts(today);
        log.info("爬取开盘啦概念结束");
    }
}
