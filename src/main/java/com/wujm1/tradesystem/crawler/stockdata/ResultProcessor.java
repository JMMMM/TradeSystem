package com.wujm1.tradesystem.crawler.stockdata;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.wujm1.tradesystem.entity.Stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * @author wujiaming
 * @date 2024-02-21 9:01
 */
public class ResultProcessor {
    public List<Stock> processor(JSONObject jsonObject, String today, String yesterday) {
        JSONArray datas = jsonObject.getJSONObject("answer").getJSONArray("components").getJSONObject(0)
                .getJSONObject("data").getJSONArray("datas");
        Object[] objects = datas.stream().toArray();
        List<Stock> rows = Lists.newArrayList();
        for (Object row : objects) {
            JSONObject jo_row = (JSONObject) row;
            Stock stock = new Stock();
            stock.setDate("");
            stock.setCode("");
            stock.setName("");
            stock.setOpen(new BigDecimal("0"));
            stock.setClose(new BigDecimal("0"));
            stock.setHigh(new BigDecimal("0"));
            stock.setLow(new BigDecimal("0"));
            stock.setrClose(new BigDecimal("0"));
            stock.setAmount(new BigDecimal("0"));
            stock.setVol(0L);
            stock.setAmplitude(new BigDecimal("0"));
            stock.setChg(new BigDecimal("0"));
            stock.setSwap(new BigDecimal("0"));
            stock.setReason("");
            stock.setFirstCeilingTime("");
            stock.setLastCeilingTime("");
            stock.setCeilingOpenTimes(0);
            stock.setVolRate(new BigDecimal("0"));
            stock.setFde(new BigDecimal("0"));
            stock.setFcb(new BigDecimal("0"));
            stock.setTdzlje(new BigDecimal("0"));
            stock.setCallVol(new BigDecimal("0"));
            stock.setCeilingType("");
            stock.setFreeMarket(new BigDecimal("0"));
            stock.setCeilingDays(0);
            stock.setCallVolRate(new BigDecimal("0"));
            stock.setLastDate("");
            stock.setCallAmount(new BigDecimal("0"));
            stock.setRemark("");
            stock.setConcepts("");

            for (String key : jo_row.keySet()) {
                try {
                    stock.setDate(today);
                    stock.setLastDate(yesterday);
                    if (key.contains("封单额")) {
                        stock.setFde(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                    }
                    if (key.equals("所属概念")) {
                        try {
                            stock.setConcepts(Optional.ofNullable(jo_row.getString(key).toString()).orElse("").trim());
                        } catch (Exception e) {
                            stock.setConcepts("");
                        }
                    }
                    if (key.contains("涨跌幅")) {
                        try {
                            stock.setChg(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setChg(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("股票代码")) {
                        String code = jo_row.getString(key);
                        stock.setCode(code);
                    }
                    if (key.contains("收盘价")) {
                        BigDecimal close = jo_row.getBigDecimal(key);
                        close = close == null ? BigDecimal.ZERO : close;
                        if (key.contains(today)) {
                            stock.setClose(close);
                        } else {
                            stock.setrClose(close);
                        }
                    }
                    if (key.contains("实际换手率")) {
                        try {
                            stock.setSwap(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setSwap(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("涨停封单量占成交量比")) {
                        stock.setFcb(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                    }
                    if (key.contains("竞价量")) {
                        try {
                            stock.setCallVol(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setCallVol(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("几天几板")) {
                        try {
                            stock.setCeilingType(jo_row.getString(key).trim());
                        } catch (Exception ex) {
                            stock.setCeilingType("");
                        }
                    }
                    if (key.contains("最高价")) {
                        try {
                            stock.setHigh(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setHigh(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("成交量[" + today + "]")) {
                        try {
                            stock.setVol(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP).longValue());
                        } catch (Exception ex) {
                            stock.setVol(0L);
                        }
                    }
                    if (key.contains("涨停开板次数")) {
                        stock.setCeilingOpenTimes(jo_row.getInteger(key));
                    }
                    if (key.contains("首次涨停时间")) {
                        stock.setFirstCeilingTime(jo_row.getString(key).trim());
                    }
                    if (key.contains("竞价金额")) {
                        try {
                            stock.setCallAmount(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setCallAmount(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("股票简称")) {
                        stock.setName(jo_row.getString(key).trim());
                    }
                    if (key.contains("涨停原因")) {
                        String reason = jo_row.getString(key);
                        try {
                            stock.setReason(StringUtils.equals(reason, "null") ? "" : reason.trim());
                        } catch (Exception ex) {
                            stock.setReason("");
                        }
                    }
                    if (key.contains("最终涨停时间")) {
                        stock.setLastCeilingTime(jo_row.getString(key).trim());
                    }
                    if (key.contains("开盘价")) {
                        try {
                            stock.setOpen(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));

                        } catch (Exception ex) {
                            stock.setOpen(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("成交额")) {
                        try {
                            stock.setAmount(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setAmount(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("最新涨跌幅")) {
                        stock.setChg(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                    }
                    if (key.equals("量比[" + today + "]")) {
                        BigDecimal vol_rate = jo_row.getBigDecimal(key);
                        stock.setVolRate(vol_rate == null ? BigDecimal.ZERO : vol_rate.setScale(2, RoundingMode.HALF_UP));
                    }
                    if (key.contains("分时量比")) {
                        try {
                            BigDecimal call_vol_rate = jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP);
                            stock.setCallVolRate(call_vol_rate);
                        } catch (Exception ex) {
                            stock.setCallVolRate(BigDecimal.ZERO);
                        }

                    }
                    if (key.contains("振幅")) {
                        try {
                            stock.setAmplitude(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setAmplitude(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("自由流通市值")) {
                        try {
                            stock.setFreeMarket(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));

                        } catch (Exception ex) {
                            stock.setFreeMarket(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("最低价")) {
                        try {
                            stock.setLow(jo_row.getBigDecimal(key).setScale(2, RoundingMode.HALF_UP));
                        } catch (Exception ex) {
                            stock.setLow(BigDecimal.ZERO);
                        }
                    }
                    if (key.contains("连续涨停天数")) {
                        stock.setCeilingDays(jo_row.getInteger(key));
                    }
                    if (key.contains("竞价特大单净额")) {
                        BigDecimal tdzlje = jo_row.getBigDecimal(key);
                        stock.setTdzlje(tdzlje == null ? BigDecimal.ZERO : tdzlje.setScale(2,
                                RoundingMode.HALF_UP));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            rows.add(stock);
        }
        return rows;
    }
}
