package com.wujm1.tradesystem.crawler.kpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.Concept;
import com.wujm1.tradesystem.entity.StockKpl;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.ext.ConceptMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockKplMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import com.wujm1.tradesystem.utils.OkClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author wujiaming
 * @date 2025-02-10 18:38
 */
@Component
@Slf4j
public class KaipanlaConceptsCrawler {

    @Autowired
    private WencaiConditionMapperExt wencaiConditionMapperExt;

    @Autowired
    private ConceptMapperExt conceptMapperExt;

    public List<Concept> initKaipanlaConcepts(String stockCode) {
        WencaiCondition wencaiCondition = wencaiConditionMapperExt.selectByPrimaryKey("kaipanla");
        String url = JSONObject.parseObject(wencaiCondition.getCondition()).getString("url");
        List<Concept> result = Lists.newArrayList();
        try {
            String json = OkClientUtils.get(String.format(url, stockCode));
            result.addAll(parse(stockCode, json));
        } catch (IOException e) {
            log.error("请求开盘啦概念失败:原因{}", e.getMessage(), e);
        }
        conceptMapperExt.saveOrUpdateBatch(result);
        return result;
    }

    public List<Concept> parse(String stockCode, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("info").getJSONArray(0);
        List<Concept> result = Lists.newArrayList();
        for (int i = 0; i < data.size(); i++) {
            JSONArray row = data.getJSONArray(i);
            Concept concept1 = new Concept();
            concept1.setCode(stockCode);
            concept1.setConcept(row.getString(1));
            result.add(concept1);
        }
        return result;
    }
}
