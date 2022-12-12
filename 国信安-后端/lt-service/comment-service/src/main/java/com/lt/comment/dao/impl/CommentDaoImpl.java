package com.lt.comment.dao.impl;

import com.lt.comment.dao.CommentDao;
import com.lt.utils.SnowFlakeUtil;
import com.lt.model.comment.Comment;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论impl
 *
 * @author Lhz
 * @date 2022/11/20
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentDaoImpl implements CommentDao {

    /**
     * 蒙戈模板
     */
    private final MongoTemplate mongoTemplate;
    /**
     * 雪花跑龙套
     */
    private final SnowFlakeUtil snowFlakeUtil;

    /**
     * 找到所有
     *
     * @param debateId 辩论id
     * @param pageSize 页面大小
     * @param begin    开始
     * @return {@link List}<{@link Comment}>
     */
    @Override
    public List<Comment> findAll(String debateId, Integer pageSize, Integer begin) {
        //从skip开始 返回limit页
        final Query limitQuery = new Query();
        limitQuery
                .with(Sort.by(Sort.Order.asc(Comment.Fields.likes)));
        limitQuery.addCriteria(
                Criteria.where(Comment.Fields.debateId).is(debateId)
        );
        return mongoTemplate.find(limitQuery, Comment.class);
    }

    /**
     * 添加像
     *
     * @param id id
     * @return long
     */
    @Override
    public long addLike(String id) {
        final Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(id)
        );
        Comment comment = mongoTemplate.findOne(query, Comment.class);
        Update update = new Update();
        assert comment != null;
        update.inc(Comment.Fields.likes, 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Comment.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public long reduceLike(String id) {
        final Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(id)
        );
        final Comment comment = mongoTemplate.findOne(query, Comment.class);
        assert comment != null;
        if(comment.getLikes() == 0L){
            return -1;
        }
        final Update update = new Update();
        update.inc(Comment.Fields.likes, -1);
        final UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Comment.class);
        return updateResult.getModifiedCount();
    }

    /**
     * 删除评论
     * @param id id
     */
    @Override
    public void deleteComment(String id) {
        final Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(id)
        );
        mongoTemplate.remove(query, Comment.class);

    }

    /**
     * 插入评论
     * @param username  用户名
     * @param debateId   辩论id
     * @param nickname   昵称
     * @param userImage  用户头像
     * @param title      标题
     * @param content    内容
     * @param createTime 创建时间
     */
    @Override
    public void insertComment(String commentId, String username, String debateId, String nickname, String userImage, String title, String content, String createTime) {
        Comment comment = new Comment();
        comment.setUsername(username);
        comment.setCommentId(commentId);
        comment.setSubComments(0L);
        comment.setLikes(0L);
        comment.setNickname(nickname);
        comment.setContent(content);
        comment.setCreateTime(createTime);
        comment.setDebateId(debateId);
        comment.setUserImage(userImage);
        mongoTemplate.insert(comment);
    }

    /**
     * 发现通过评论id
     *
     * @param commentId 评论id
     * @return {@link Comment}
     */
    public Comment findByCommentId(String commentId) {
        Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(commentId)
        );
        return mongoTemplate.findOne(query, Comment.class);
    }

    /**
     * 添加子评论数
     * @param commentId 评论id
     */
    public synchronized void addSubCommentCount(String commentId) {
        Comment comment = this.findByCommentId(commentId);
        Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(commentId)
        );
        Update update = new Update();
        assert comment != null;
        update.inc(Comment.Fields.subComments, 1);
        mongoTemplate.upsert(query, update, Comment.class);
    }

    /**
     * 删除子评论数
     * @param commentId 评论id
     */
    public synchronized void deleteSubCommentCount(String commentId) {
        Comment comment = this.findByCommentId(commentId);
        Query query = Query.query(
                Criteria.where(Comment.Fields.commentId).is(commentId)
        );
        Update update = new Update();
        assert comment != null;
        update.inc(Comment.Fields.subComments, -1);
        mongoTemplate.upsert(query, update, Comment.class);
    }
}
