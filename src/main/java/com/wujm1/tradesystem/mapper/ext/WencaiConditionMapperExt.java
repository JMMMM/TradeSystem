package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.WencaiCondition;
import com.wujm1.tradesystem.mapper.WencaiConditionMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WencaiConditionMapperExt extends WencaiConditionMapper {

    List<WencaiCondition> selectAll();
}