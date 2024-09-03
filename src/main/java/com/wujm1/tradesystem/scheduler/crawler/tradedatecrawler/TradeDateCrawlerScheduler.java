package com.wujm1.tradesystem.scheduler.crawler.tradedatecrawler;

import com.wujm1.tradesystem.crawler.tradedate.TradeDateCrawler;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wujiaming
 * @date 2024-09-02 18:17
 * @description 交易日爬虫调度器
 */
@Component
@Slf4j
public class TradeDateCrawlerScheduler {

    private final TradeDateCrawler tradeDateCrawler;

    public TradeDateCrawlerScheduler(TradeDateCrawler tradeDateCrawler) {
        this.tradeDateCrawler = tradeDateCrawler;
    }

    @Scheduled(cron = "0 0 2 1 1 ?")
    public void run() {
        Date date = new Date();
        String thisYear = DateUtils.getDateStr(DateUtils.getYear(date, 0), "yyyy");
        String scheduler_name = String.format("爬取%s年交易日数据", thisYear);
        log.info(scheduler_name + "开始");
        tradeDateCrawler.initTradeDate(thisYear);
        log.info(scheduler_name + "结束");
    }

}
