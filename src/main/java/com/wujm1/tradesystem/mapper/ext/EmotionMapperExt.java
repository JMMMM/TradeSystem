package com.wujm1.tradesystem.mapper.ext;

import com.wujm1.tradesystem.entity.Emotion;
import com.wujm1.tradesystem.mapper.EmotionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EmotionMapperExt extends EmotionMapper {

    List<Emotion> emotionQuery(@Param("startDate") String startDate);
}