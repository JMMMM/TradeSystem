package com.wujm1.tradesystem.crawler.jiuyangongshe;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.StockStatistics;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.StockStatisticsMapper;
import com.wujm1.tradesystem.mapper.ext.StockStatisticsMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import com.wujm1.tradesystem.utils.DateUtils;
import com.wujm1.tradesystem.utils.StockCodeFormatter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author wujiaming
 * @date 2024-11-28 14:25
 */
@Component
@Slf4j
public class JiuyangongsheCrawler {

    @Autowired
    private StockStatisticsMapperExt stockStatisticsMapperext;
    @Autowired
    private WencaiConditionMapperExt wencaiConditionMapperExt;
    @Autowired
    private TradeDateMapperExt tradeDateMapperExt;

    public List initJiuyangongshe(String yyyyMMdd) {
        TradeDate tradedate = tradeDateMapperExt.selectByPrimaryKey(yyyyMMdd);
        if (Objects.isNull(tradedate)) {
            log.error("日期{}不是交易日", yyyyMMdd);
            return null;
        }
        log.info("韭菜公社数据爬取，日期：{}", yyyyMMdd);
        WencaiCondition wencaiCondition = wencaiConditionMapperExt.selectByPrimaryKey("jiuyangongshe_cookies");
        JSONObject wencaiCookies = JSONObject.parseObject(wencaiCondition.getCondition());
        String yyyyMMdd2 = DateUtils.changeDateFormat(yyyyMMdd, "yyyyMMdd", "yyyy-MM-dd");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String bodyStr = "{\"date\":\"" + yyyyMMdd2 + "\",\"pc\":1}";
        RequestBody body = RequestBody.create(mediaType, bodyStr);
        Request request = new Request.Builder()
                .url(wencaiCookies.getString("url"))
                .method("POST", body)
                .addHeader("sec-ch-ua-platform", "\"Windows\"")
                .addHeader("timestamp", System.currentTimeMillis() + "")
                .addHeader("sec-ch-ua", "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"")
                .addHeader("sec-ch-ua-mobile", "?0")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/json")
                .addHeader("token", wencaiCookies.getString("token"))
                .addHeader("platform", "3")
                .addHeader("Sec-Fetch-Site", "same-site")
                .addHeader("Sec-Fetch-Mode", "cors")
                .addHeader("Sec-Fetch-Dest", "empty")
                .addHeader("host", "app.jiuyangongshe.com")
                .addHeader("Cookie", wencaiCookies.getString("Cookie"))
                .build();
        Response response = null;
        String json = null;
        try {
            response = client.newCall(request).execute();
            json = response.body().string();
        } catch (IOException e) {
            log.error("请求韭研公社接口失败:原因{}", e.getMessage(), e);
        }
        JSONArray data = JSONObject.parseObject(json).getJSONArray("data");
        List<StockStatistics> result = Lists.newArrayList();
        for (int i = 1; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String groupName = jsonObject.getString("name");
            JSONArray list = jsonObject.getJSONArray("list");
            for (int j = 0; j < list.size(); j++) {
                JSONObject stock = list.getJSONObject(j);
                StockStatistics stockStatistics = new StockStatistics();
                stockStatistics.setDate(yyyyMMdd);
                //sz002846
                String code = StockCodeFormatter.format(stock.getString("code"));
                stockStatistics.setCode(code);
                String name = StockCodeFormatter.format(stock.getString("name"));
                stockStatistics.setName(name);
                stockStatistics.setGroupName(groupName);
                JSONObject action_info = stock.getJSONObject("article").getJSONObject("action_info");
                stockStatistics.setCeilingDays(action_info.getInteger("day"));
                String ceilingType = action_info.getString("num");
                stockStatistics.setCeilingType(ceilingType);
                String ceilingTime = action_info.getString("time");
                stockStatistics.setCeilingTime(ceilingTime);
                String expound = action_info.getString("expound");
                stockStatistics.setReason(expound);
                result.add(stockStatistics);
            }
        }
        stockStatisticsMapperext.saveOrUpdateBatch(result);
        log.info("韭菜公社数据爬取结束");

        return result;
    }
}
