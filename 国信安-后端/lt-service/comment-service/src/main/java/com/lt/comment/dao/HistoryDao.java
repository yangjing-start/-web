package com.lt.comment.dao;

import com.lt.model.comment.History;

import java.util.List;

/**
 * @author Lhz
 */
public interface HistoryDao {

    /**
     * 插入历史记录
     *
     * @param debateId       debateId
     * @param debateNickname nickname
     * @param userImage      userImage
     * @param title          标题
     * @param username       用户名
     */
    void insertHistory(String username, String debateId, String debateNickname, String userImage, String title);

    /**
     * 删除历史记录
     *
     * @param debateId debateId
     */
    void deleteHistory(String debateId);

    /**
     * 分页查询历史记录
     *
     * @param username  username
     * @param pageSize  pageSize
     * @param beginPage beginPage
     * @return 历史记录
     */
    List<History> findAllHistory(String username, Integer pageSize, Integer beginPage);

    /**
     * 查找该用户历史记录
     * @param username 用户名
     * @param debateId debateId
     */
    Boolean findHistory(String username, String debateId);
}
