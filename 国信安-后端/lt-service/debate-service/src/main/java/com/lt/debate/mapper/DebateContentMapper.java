package com.lt.debate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.model.debate.pojo.DebateContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Author WanXin
 * @Date 2022/11/17
 */
@Mapper
public interface DebateContentMapper extends BaseMapper<DebateContent> {


    List<DebateContent> findContentListByLast5days(@Param("dayParam") Date dayParam);

}
