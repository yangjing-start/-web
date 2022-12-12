package com.lt.comment.dao;

import com.lt.model.comment.SubComment;

import java.util.List;

/**
 * @author Lhz
 */

public interface SubCommentDao {

    /**
     * 删除子评论
     *
     * @param id  该评论Id
     * @param fid fId
     */
    void deleteSubComment(String fid, String id);

    /**
     * 增加子评论
     *
     * @param fId       父Id
     * @param reply     回复对象
     * @param content   回复内容
     * @param nickname  该条评论的用户昵称
     * @param userImage 该条评论的用户头像
     */
    void insertSubComment(String fId, String reply, String content, String nickname, String userImage, String username);

    /**
     * 根据以及评论分页查找二级评论
     *
     * @param fId      父评论Id
     * @param pageSize 分页数量
     * @param begin    开始位
     * @return 评论
     */
    List<SubComment> findAll(String fId, Integer pageSize, Integer begin);

    /**
     * 点赞数加一
     *
     * @param id 该评论Id
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
     * 查找当前子评论
     *
     * @param subCommentId subCommentId
     * @return subCommentId
     */
    SubComment findBySubCommentId(String subCommentId);

//    /**
//     * @param username 该条评论发布者
//     * @return Comment
//     */
//    SubComment findByUsername(String username);
    /**
     * 删除该一级评论下的错you子评论
     *
     * @param fId
     */
    void deleteAllSubComment(String fId);
}
