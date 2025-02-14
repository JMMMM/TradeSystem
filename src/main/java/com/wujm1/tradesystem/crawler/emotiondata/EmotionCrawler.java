package com.wujm1.tradesystem.crawler.emotiondata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wujm1.tradesystem.entity.Emotion;
import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.ext.EmotionMapperExt;
import com.wujm1.tradesystem.mapper.ext.TradeDateMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author wujiaming
 * @date 2024-03-06 15:22
 */

@Component
@Slf4j
public class EmotionCrawler {
    /**
     * 年度法定节假日、公休日查询接口地址
     */
    private static final String wencai_url = "https://www.iwencai.com/gateway/urp/v7/landing/getDataList?iwcpro=1";

    private final WencaiConditionMapperExt wencaiConditionMapperExt;
    private final EmotionMapperExt emotionMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;
    /**
     * http请求工具类
     */
    private static final RestTemplate restTemplate = new RestTemplate();

    public EmotionCrawler(WencaiConditionMapperExt wencaiConditionMapperExt, EmotionMapperExt emotionMapperExt, TradeDateMapperExt tradeDateMapperExt) {
        this.wencaiConditionMapperExt = wencaiConditionMapperExt;
        this.emotionMapperExt = emotionMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
    }

    public JSONObject crawler(String today2, String today1, String yesterday2) {
        Map<String, WencaiCondition> wencaiConditionMap = wencaiConditionMapperExt.selectAll().stream().collect(Collectors.toMap(WencaiCondition::getConditionName, Function.identity()));
        String cookies = wencaiConditionMap.get("cookies").getCondition();
        String query = wencaiConditionMap.get("emotion_query").getCondition();
        String condition = wencaiConditionMap.get("emotion_condition").getCondition();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookies);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("query", query.replaceAll("today2", today2).replaceAll("today1", today1).replaceAll("yesterday2", yesterday2));
        params.add("condition", condition.replaceAll("today2", today2).replaceAll("today1", today1).replaceAll("yesterday2", yesterday2));
        params.add("urp_sort_index", "指数代码");
        params.add("query_type", "zhishu");
        params.add("source", "Ths_iwencai_Xuangu");
        params.add("comp_id", "6829723");
        params.add("uuid", "24089");
        params.add("perpage", "100");
        params.add("page", 1);
        params.add("logid", "d99e29c29d58a269defdaa7d18ab7e5a");
        params.add("ret", "json_all");
        params.add("user_id", "Ths_iwencai_Xuangu_xqvhlculvxjnw4osr41up6c3al8um9q4");
        params.add("business_cat", "soniu");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(wencai_url, httpEntity, String.class);
        return JSON.parseObject(responseEntity.getBody());
    }

    public void initEmotion(String today2) {
        //获取最近两个交易日
        List<TradeDate> tradeDateLast10 = tradeDateMapperExt.selectByDateTopN(today2, 10);
        List<String> dates = tradeDateLast10.stream().map(TradeDate::getDate).collect(Collectors.toList());
        if (!dates.contains(today2)) {
            log.info("当日不是交易日，不拉取数据");
            return;
        }
        List<TradeDate> tradeDateList = tradeDateMapperExt.selectByDateTopN(today2, 2);
        String yesterday2 = tradeDateList.get(0).getDate();
        String today1 = tradeDateList.get(1).getYear() + "年" + tradeDateList.get(0).getMonth() + "月" + tradeDateList.get(0).getDay() + "日";
        JSONObject jsonObject = this.crawler(today2, today1, yesterday2);
        JSONArray datas = jsonObject.getJSONObject("answer").getJSONArray("components").getJSONObject(0)
                .getJSONObject("data").getJSONArray("datas");
        JSONObject row = datas.getJSONObject(0);
        BigDecimal open = row.getBigDecimal(String.format("指数@开盘价:不复权[%s]", today2));
        BigDecimal close = row.getBigDecimal(String.format("指数@收盘价:不复权[%s]", today2));
        BigDecimal high = row.getBigDecimal(String.format("指数@最高价:不复权[%s]", today2));
        BigDecimal low = row.getBigDecimal(String.format("指数@最低价:不复权[%s]", today2));
        BigDecimal ma3 = row.getBigDecimal(String.format("3日指数@均线[%s]", today2));
        BigDecimal ma5 = row.getBigDecimal(String.format("5日指数@均线[%s]", today2));
        BigDecimal chg = row.getBigDecimal(String.format("指数@涨跌幅:前复权[%s]", today2));

        Emotion emotion = new Emotion();
        emotion.setDate(today2);
        emotion.setLastDate(yesterday2);
        emotion.setOpen(open);
        emotion.setClose(close);
        emotion.setHigh(high);
        emotion.setLow(low);
        emotion.setMa3(ma3);
        emotion.setMa5(ma5);
        emotion.setChg(chg);
        emotionMapperExt.insertOrUpdate(emotion);

        log.info("插入情绪数据成功:{}", emotion.toString());


    }

}
