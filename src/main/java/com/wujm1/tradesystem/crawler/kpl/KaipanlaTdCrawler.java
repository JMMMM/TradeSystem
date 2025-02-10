package com.wujm1.tradesystem.crawler.kpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.StockKpl;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.ext.StockKplMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author wujiaming
 * @date 2025-02-10 17:24
 */
@Component
@Slf4j
public class KaipanlaTdCrawler {

    @Autowired
    private WencaiConditionMapperExt wencaiConditionMapperExt;

    @Autowired
    private StockKplMapperExt stockKplMapperExt;

    public List<StockKpl> initKaipanla(String yyyyMMdd) {
        WencaiCondition wencaiCondition = wencaiConditionMapperExt.selectByPrimaryKey("kaipanla");
        String url = JSONObject.parseObject(wencaiCondition.getCondition()).getString("url");
        String yyyyMMdd2 = DateUtils.changeDateFormat(yyyyMMdd, "yyyyMMdd", "yyyy-MM-dd");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        List<StockKpl> result = Lists.newArrayList();

        for (int i = 1; i <= 5; i++) {
            Request request = new Request.Builder()
                    .url(String.format(url, yyyyMMdd2, 1)).get().build();
            Response response = null;
            String json = null;
            try {
                response = client.newCall(request).execute();
                json = response.body().string();
                List<StockKpl> parse = parse(yyyyMMdd, json);
                result.addAll(parse);
            } catch (IOException e) {
                log.error("请求开盘啦失败:原因{}", e.getMessage(), e);
            }
        }
        stockKplMapperExt.saveOrUpdateBatch(result);

        return result;
    }

    public List<StockKpl> parse(String yyyyMMdd, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("info").getJSONArray(0);
        List<StockKpl> result = Lists.newArrayList();
        for (int i = 0; i < data.size(); i++) {
            JSONArray stock = data.getJSONArray(i);
            StockKpl stockKpl = new StockKpl();
            stockKpl.setDate(yyyyMMdd);
            stockKpl.setCode(stock.getString(0));
            stockKpl.setName(stock.getString(1));
            stockKpl.setCeilingDays(stock.getInteger(15));
            stockKpl.setLastCeilingTime(stock.getString(4));
            stockKpl.setGroupName(stock.getString(5));
            stockKpl.setFde(stock.getBigDecimal(6));
            stockKpl.setMaxFed(stock.getBigDecimal(7));
            stockKpl.setZlje(stock.getBigDecimal(8));
            stockKpl.setZlmr(stock.getBigDecimal(9));
            stockKpl.setZlmc(stock.getBigDecimal(10).abs());
            stockKpl.setAmount(stock.getBigDecimal(11));
            stockKpl.setConcept(stock.getString(12));
            stockKpl.setAmplitude(stock.getBigDecimal(17));
            stockKpl.setFreeMarket(stock.getBigDecimal(13));
            stockKpl.setSwap(stock.getBigDecimal(14));
            result.add(stockKpl);
        }
        return result;
    }

    public static void main(String[] args) {
        KaipanlaTdCrawler kaipanlaTdCrawler = new KaipanlaTdCrawler();
        kaipanlaTdCrawler.initKaipanla("20250210");
    }
}
