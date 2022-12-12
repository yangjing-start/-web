package com.lt.debate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.model.debate.pojo.DebateKind;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DebateKindMapper extends BaseMapper<DebateKind> {

    List<DebateKind> getKinds();
}
