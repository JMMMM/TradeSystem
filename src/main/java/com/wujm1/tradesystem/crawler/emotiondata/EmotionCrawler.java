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
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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

    private final WencaiConditionMapperExt wencaiConditionMapperExt;
    private final EmotionMapperExt emotionMapperExt;
    private final TradeDateMapperExt tradeDateMapperExt;

    public EmotionCrawler(WencaiConditionMapperExt wencaiConditionMapperExt, EmotionMapperExt emotionMapperExt, TradeDateMapperExt tradeDateMapperExt) {
        this.wencaiConditionMapperExt = wencaiConditionMapperExt;
        this.emotionMapperExt = emotionMapperExt;
        this.tradeDateMapperExt = tradeDateMapperExt;
    }

    public JSONObject crawler(String today2, String today1, String yesterday2) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Map<String, WencaiCondition> wencaiConditionMap = wencaiConditionMapperExt.selectAll().stream().collect(Collectors.toMap(WencaiCondition::getConditionName, Function.identity()));
        String query = wencaiConditionMap.get("emotion_query").getCondition().replaceAll("today2", today2).replaceAll("today1", today1).replaceAll("yesterday2", yesterday2);
        String condition = wencaiConditionMap.get("emotion_condition").getCondition().replaceAll("today2", today2).replaceAll("today1", today1).replaceAll("yesterday2", yesterday2);
        String wencai_url = wencaiConditionMap.get("emtion_url").getCondition();
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("query", query)
                .addFormDataPart("condition", condition)
                .addFormDataPart("query_type", "stock")
                .addFormDataPart("source", "Ths_iwencai_Xuangu")
                .addFormDataPart("comp_id", "6836372")
                .addFormDataPart("uuid", "24087")
                .addFormDataPart("perpage", "100")
                .addFormDataPart("page", "1")
                .build();
        Request request = new Request.Builder()
                .url(wencai_url)
                .method("POST", body)
                .addHeader("Cookie", "other_uid=Ths_iwencai_Xuangu_xqvhlculvxjnw4osr41up6c3al8um9q4; ta_random_userid=ukoetmp31g; cid=61f00c6cae4404a7c5dc2a67ff81f10a1704935377; cid=61f00c6cae4404a7c5dc2a67ff81f10a1704935377; ComputerID=61f00c6cae4404a7c5dc2a67ff81f10a1704935377; WafStatus=0; guideState=1; user=MDpteF81MzIyMjI5MzE6Ok5vbmU6NTAwOjU0MjIyMjkzMTo3LDExMTExMTExMTExLDQwOzQ0LDExLDQwOzYsMSw0MDs1LDEsNDA7MSwxMDEsNDA7MiwxLDQwOzMsMSw0MDs1LDEsNDA7OCwwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMSw0MDsxMDIsMSw0MDoxNjo6OjUzMjIyMjkzMToxNzA2MjU3ODcxOjo6MTU5NDM0NTY4MDoyNjc4NDAwOjA6MTY2YWU2ODM5ZWZjODQyMjZhZTI3OWQ0MTA4MDk5OGEwOmRlZmF1bHRfNDow; userid=532222931; u_name=mx_532222931; escapename=mx_532222931; ticket=db855d2ed9f1dc94d4de9afdd2711c3a; user_status=0; utk=06ab70f98e44c4a4806e032824c24f00; wencai_pc_version=1; v=A1JP610FYznzvp_FHb3x491aoxMxY1kJiElKaR2eeuzwcvyNBPOmDVj3moXv")
                .build();
        Response response = null;
        try {
            log.info("emotion query:{}  condition :{} ", query, condition);
            response = client.newCall(request).execute();
            String result = response.body().string();
            log.info("emotion result:{}", result);
            return JSON.parseObject(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        String today1 = tradeDateList.get(1).getYear() + "年" + tradeDateList.get(1).getMonth() + "月" + tradeDateList.get(1).getDay() + "日";
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
