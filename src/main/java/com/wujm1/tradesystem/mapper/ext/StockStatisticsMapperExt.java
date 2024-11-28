package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.entity.StockStatistics;
import com.wujm1.tradesystem.mapper.StockStatisticsMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StockStatisticsMapperExt extends StockStatisticsMapper {
    void saveOrUpdateBatch(@Param("list") List<StockStatistics> stocks);

}