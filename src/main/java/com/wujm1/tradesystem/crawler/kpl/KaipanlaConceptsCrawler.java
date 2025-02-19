package com.wujm1.tradesystem.crawler.kpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.Concept;
import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.StockKpl;
import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.ext.ConceptMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockKplMapperExt;
import com.wujm1.tradesystem.mapper.ext.StockMapperExt;
import com.wujm1.tradesystem.mapper.ext.WencaiConditionMapperExt;
import com.wujm1.tradesystem.utils.OkClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private StockMapperExt stockMapperExt;

    public void initKaipanlaConcepts(String yyyyMMdd) {
        List<Stock> stocks = stockMapperExt.queryStockByDates(null, yyyyMMdd, yyyyMMdd);
        WencaiCondition wencaiCondition = wencaiConditionMapperExt.selectByPrimaryKey("kaipanla");
        String url = JSONObject.parseObject(wencaiCondition.getCondition()).getString("concepts_url");
        for (Stock stock : stocks) {
            initCore(url, stock.getCode());
        }
    }

    public List<Concept> initCore(String stockCode) {
        WencaiCondition wencaiCondition = wencaiConditionMapperExt.selectByPrimaryKey("kaipanla");
        String url = JSONObject.parseObject(wencaiCondition.getCondition()).getString("concepts_url");
        return initCore(url, stockCode);
    }

    public List<Concept> initCore(String url, String stockCode) {
        String origin = stockCode;
        stockCode = stockCode.substring(0, 6);
        List<Concept> result = Lists.newArrayList();
        try {
            String json = OkClientUtils.get(String.format(url, stockCode));
            result.addAll(parse(origin, json));
        } catch (IOException e) {
            log.error("请求开盘啦概念失败:原因{}", e.getMessage(), e);
        }
        if (!CollectionUtils.isEmpty(result)) {
            log.info("获取概念数据code:{}，概念:{}", stockCode, result.stream().map(Concept::getConcept).collect(Collectors.toList()));
            conceptMapperExt.saveOrUpdateBatch(result);
        }
        return result;
    }

    public List<Concept> parse(String stockCode, String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray data = jsonObject.getJSONArray("info");
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
