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

    List<Stock> queryCybCeilingStock(@Param("date") String date, @Param("ceilingDays") Integer ceilingDays);

    List<Stock> queryStockByCodes(@Param("codes") List<String> codes, @Param("date") String date);

    List<Stock> queryDragonStock(@Param("startDate") String startDate, @Param("endDate") String endDate);

    Stock queryStockByName(@Param("name") String name, @Param("date") String date);
}
