package com.wujm1.tradesystem.crawler.stockdata;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author wujiaming
 * @date 2024-09-03 10:08
 */
@Component
@Slf4j
public class WencaiCrawler {
    private static final String wencai_url = "https://www.iwencai.com/gateway/urp/v7/landing/getDataList?iwcpro=1";
    private final RestTemplate crawlerRestTemplate;

    public WencaiCrawler(RestTemplate crawlerRestTemplate) {
        this.crawlerRestTemplate = crawlerRestTemplate;
    }

    public JSONObject crawler(String query, String condition, String cookies, int page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookies);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.add("query", query);
        params.add("condition", condition);
        params.add("page", page);
        params.add("query_type", "stock");
        params.add("source", "Ths_iwencai_Xuangu");
        params.add("comp_id", "6836372");
        params.add("uuid", "24087");
        params.add("perpage", "100");
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(params, headers);
        ResponseEntity<String> responseEntity = crawlerRestTemplate.postForEntity(wencai_url, httpEntity, String.class);
        return JSON.parseObject(responseEntity.getBody());
    }

    public static String readSource(String filePath) {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return "";
    }
}
