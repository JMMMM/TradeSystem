package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.Stock;
import com.wujm1.tradesystem.mapper.StockMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Desctiption <Template>
 * @Author wujiaming
 * @Date 2024/9/2
 */
@Mapper
public interface StockMapperExt extends StockMapper {

    void saveOrUpdateBatch(@Param("list") List<Stock> stocks);


    List<Stock> queryCeilingDays(@Param("startDate") String startDate, @Param("minCeilingDays") int minCeilingDays);

    List<Stock> queryStockByDates(@Param("code") String code, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
