package com.lt.comment.dao;

import com.lt.model.comment.Comment;

import java.util.List;

/**
 * @author Lhz
 */
public interface CommentDao {

    /**
     * 根据当前页的id分页查找所有一级评论
     *
     * @param id       当前页的id
     * @param pageSize 分页数量
     * @param begin    开始位
     * @return 评论
     */
    List<Comment> findAll(String id, Integer pageSize, Integer begin);

    /**
     * 点在数量加一
     *
     * @param id 该评论的id
     * @return 修改数量
     */
    long addLike(String id);

    /**
     * 点赞数减一
     *
     * @param id 该评论Id
     * @return 修改数量
     */
    long reduceLike(String id);
    /**
     * 删除当前一级评论
     * @param id 该评论的id
     */
    void deleteComment(String id);

    /**
     * 增加评论
     *
     * @param username   该条评论的username
     * @param nickname   该条评论的用户昵称
     * @param userImage  该条评论的用户头像
     * @param title      该条评论的标题
     * @param content    该条评论的内容
     * @param createTime 该条评论的创建时间
     * @param debateId   debateId
     */
    void insertComment(String commentId, String username, String debateId, String nickname, String userImage, String title, String content, String createTime);

    /**
     * @param commentId 该条评论Id
     * @return Comment
     */
    Comment findByCommentId(String commentId);


    /**
     * @param commentId 该条评论Id
     */
    void addSubCommentCount(String commentId);
    
    /**
     * @param commentId 该条评论Id
     */
    void deleteSubCommentCount(String commentId);


}
