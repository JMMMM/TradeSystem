package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.TradeDate;
import com.wujm1.tradesystem.mapper.TradeDateMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TradeDateMapperExt extends TradeDateMapper {

    void saveOrUpdateBatch(@Param("list") List<TradeDate> tradeDates);

    List<TradeDate> selectByDateTopN(@Param("date") String date, @Param("topN") int topN);

    List<TradeDate> showAllTradeDate(@Param("startDate") String startDate,@Param("endDate") String endDate);
}