package com.wujm1.tradesystem.scheduler.crawler.stockcrawler;

import com.wujm1.tradesystem.crawler.emotiondata.EmotionCrawler;
import com.wujm1.tradesystem.crawler.jiuyangongshe.JiuyangongsheCrawler;
import com.wujm1.tradesystem.crawler.kpl.KaipanlaTdCrawler;
import com.wujm1.tradesystem.crawler.stockdata.WencaiConditionCrawler;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author wujiaming
 * @date 2024-09-02 16:48
 */
@Component
@Slf4j
public class AutoRunnerScheduler {
    private final WencaiConditionCrawler wencaiConditionCrawler;
    private final EmotionCrawler emotionCrawler;
    private final JiuyangongsheCrawler jiuyangongsheCrawler;
    private final KaipanlaTdCrawler kaipanlaTdCrawler;

    public AutoRunnerScheduler(WencaiConditionCrawler wencaiConditionCrawler, EmotionCrawler emotionCrawler, JiuyangongsheCrawler jiuyangongsheCrawler, KaipanlaTdCrawler kaipanlaTdCrawler) {
        this.wencaiConditionCrawler = wencaiConditionCrawler;
        this.emotionCrawler = emotionCrawler;
        this.jiuyangongsheCrawler = jiuyangongsheCrawler;
        this.kaipanlaTdCrawler = kaipanlaTdCrawler;
    }

    @Scheduled(cron = "0 0 16 * * ?")
    public void run() {
        String today = DateUtils.getDateStr(new Date(), "yyyyMMdd");
        log.info("爬取同花顺交易数据开始");
        wencaiConditionCrawler.initSearchCondition(today);
        log.info("爬取同花顺交易数据结束");
        log.info("爬取同花顺情绪数据开始");
        emotionCrawler.initEmotion(today);
        log.info("爬取同花顺情绪数据结束");
        log.info("爬取韭菜公社交易数据开始");
        jiuyangongsheCrawler.initJiuyangongshe(today);
        log.info("爬取韭菜公社交易数据结束");
        log.info("爬取开盘啦交易数据开始");
        kaipanlaTdCrawler.initKaipanlaTd(today);
        log.info("爬取开盘啦交易数据结束");
    }
}
