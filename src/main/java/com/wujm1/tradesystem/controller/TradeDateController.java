package com.wujm1.tradesystem.controller;

import com.wujm1.tradesystem.crawler.emotiondata.EmotionCrawler;
import com.wujm1.tradesystem.crawler.stockdata.WencaiConditionCrawler;
import com.wujm1.tradesystem.crawler.tradedate.TradeDateCrawler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public TradeDateController(TradeDateCrawler tradeDateCrawler, WencaiConditionCrawler wencaiConditionCrawler, EmotionCrawler emotionCrawler) {
        this.tradeDateCrawler = tradeDateCrawler;
        this.wencaiConditionCrawler = wencaiConditionCrawler;
        this.emotionCrawler = emotionCrawler;
    }

    @GetMapping("/init")
    public void init(@RequestParam("year") String year) {
        tradeDateCrawler.initTradeDate(year);
    }

    @GetMapping("/wencai")
    public void wencai(@RequestParam("date") String date) {
        wencaiConditionCrawler.initSearchCondition(date);
    }

    @GetMapping("/emotion")
    public void emotion(@RequestParam("date") String date) {
        emotionCrawler.initEmotion(date);
    }

}
