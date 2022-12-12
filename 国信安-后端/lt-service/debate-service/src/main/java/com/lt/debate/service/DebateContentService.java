package com.lt.debate.service;

import com.lt.model.debate.dto.ContentVisitStreamMess;
import com.lt.model.debate.dto.DebateContentDto;
import com.lt.model.debate.dto.DebatePageDto;
import com.lt.model.response.Response;

import java.util.Map;

public interface DebateContentService {

    /**
     * 获取论坛种类
     * @return
     */
    Response getKinds();

    /**
     * 根据kindId获取帖子
     * @param dto
     * @return
     */
    Response getByKind(DebatePageDto dto);

    /**
     * 获取热点
     * @return
     */
    Response getHot();

    /**
     * 搜索框suggestion
     * @param map
     * @return
     */
    Response getSuggestions(Map map);

    /**
     * 新增帖子
     * @param dto
     * @return
     */
    Response saveContent(DebateContentDto dto);

    /**
     * 根据id获取contentInfo
     * @param map
     * @return
     */
    Response getContentInfo(Map map);

    /**
     * 删除content
     * @param map
     * @return
     */
    Response deleteById(Map map);

    /**
     * 修改info
     * @param map
     * @return
     */
    Response updateInfo(Map map);

    /**
     * 更新分值
     * @param mess
     */
    void updateScore(ContentVisitStreamMess mess);

    /**
     * 计算热点文章
     */
    void computeHotArticle();

    /**
     * 根据用户id获取热点
     * @param map
     * @return
     */
    Response getHotByUser(Map map);

    /**
     * 根据用户id获取帖子
     * @param map
     * @return
     */
    Response getContentByUserId(Map map);

    /**
     * 根据用户id获取帖子
     * @param map
     * @return
     */
    Response getContentById(Map map);
}
