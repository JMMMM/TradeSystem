package com.wujm1.tradesystem.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TableData {
    private String code;
    private String name;
    private int k;
    private String startDate;
    private String endDate;
    private BigDecimal increaseRate;
    private List<String> concepts;

    private Stock minStock;

    private Stock maxStock;

    private BigDecimal minLow;

    private BigDecimal maxHigh;

    private int cnt = 1;
}
