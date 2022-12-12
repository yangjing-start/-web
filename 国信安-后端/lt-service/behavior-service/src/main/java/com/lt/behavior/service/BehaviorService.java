package com.lt.behavior.service;

import com.lt.model.debate.dto.CommentBehaviorDto;
import com.lt.model.debate.dto.ReadBehaviorDto;
import com.lt.model.response.Response;

import java.util.Map;

public interface BehaviorService {

    /**
     * 喜欢
     * @param map
     * @return
     */
    Response like(Map map);

    /**
     * 浏览
     * @param dto
     * @return
     */
    Response read(ReadBehaviorDto dto);

    /**
     * 评论
     * @param dto
     * @return
     */
    Response comment(CommentBehaviorDto dto);
}
