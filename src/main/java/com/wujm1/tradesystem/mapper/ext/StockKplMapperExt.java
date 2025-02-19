package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.StockKpl;
import com.wujm1.tradesystem.mapper.StockKplMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockKplMapperExt extends StockKplMapper {
    void saveOrUpdateBatch(@Param("list") List<StockKpl> stocks);
}