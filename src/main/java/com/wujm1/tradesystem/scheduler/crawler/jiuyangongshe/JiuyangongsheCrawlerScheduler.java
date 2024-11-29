package com.wujm1.tradesystem.scheduler.crawler.jiuyangongshe;

import com.wujm1.tradesystem.crawler.jiuyangongshe.JiuyangongsheCrawler;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wujiaming
 * @date 2024-11-28 17:13
 */
@Component
@Slf4j
public class JiuyangongsheCrawlerScheduler {

    @Autowired
    private JiuyangongsheCrawler jiuyangongsheCrawler;

    @Scheduled(cron = "0 0 16 * * ?")
    public void run() {
        String today = DateUtils.getDateStr(new Date(), "yyyyMMdd");
        log.info("爬取韭菜公社交易数据开始");
        jiuyangongsheCrawler.initJiuyangongshe(today);
        log.info("爬取韭菜公社交易数据结束");
    }
}
