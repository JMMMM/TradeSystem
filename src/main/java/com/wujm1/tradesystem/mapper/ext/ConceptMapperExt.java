package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.Concept;
import com.wujm1.tradesystem.mapper.ConceptMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConceptMapperExt extends ConceptMapper {

    void saveOrUpdateBatch(@Param("list") List<Concept> concepts);
}