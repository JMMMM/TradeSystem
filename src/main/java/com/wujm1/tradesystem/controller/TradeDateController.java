package com.wujm1.tradesystem.controller;

import com.wujm1.tradesystem.crawler.emotiondata.EmotionCrawler;
import com.wujm1.tradesystem.crawler.jiuyangongshe.JiuyangongsheCrawler;
import com.wujm1.tradesystem.crawler.stockdata.WencaiConditionCrawler;
import com.wujm1.tradesystem.crawler.tradedate.TradeDateCrawler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Desctiption <Template>
 * @Author wujiaming
 * @Date 2024/9/3
 */
@RequestMapping("/tradeDate")
@RestController
public class TradeDateController {

    private final TradeDateCrawler tradeDateCrawler;
    private final WencaiConditionCrawler wencaiConditionCrawler;
    private final EmotionCrawler emotionCrawler;
    private final JiuyangongsheCrawler jiuyangongsheCrawler;

    public TradeDateController(TradeDateCrawler tradeDateCrawler, WencaiConditionCrawler wencaiConditionCrawler, EmotionCrawler emotionCrawler, JiuyangongsheCrawler jiuyangongsheCrawler) {
        this.tradeDateCrawler = tradeDateCrawler;
        this.wencaiConditionCrawler = wencaiConditionCrawler;
        this.emotionCrawler = emotionCrawler;
        this.jiuyangongsheCrawler = jiuyangongsheCrawler;
    }

    @GetMapping("/init")
    public void init(@RequestParam("year") String year) {
        tradeDateCrawler.initTradeDate(year);
    }

    @GetMapping("/wencai")
    public void wencai(@RequestParam("date") String date) {
        String[] dates = date.split(",");
        for (String d : dates) {
            wencaiConditionCrawler.initSearchCondition(d);
        }
    }

    @GetMapping("/emotion")
    public void emotion(@RequestParam("date") String date) {
        emotionCrawler.initEmotion(date);
    }

    @GetMapping("/jiuyangongshe")
    public List jiuyangongshe(@RequestParam("date") String date) {
        return jiuyangongsheCrawler.initJiuyangongshe(date);
    }
}
