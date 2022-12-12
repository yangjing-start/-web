package com.lt.comment.dao.impl;

import com.lt.comment.dao.SubCommentDao;
import com.lt.utils.SnowFlakeUtil;
import com.lt.model.comment.SubComment;
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
 * @author Lhz
 */

@Slf4j
@Repository
@RequiredArgsConstructor
public class SubCommentDaoImpl implements SubCommentDao {

    private final MongoTemplate mongoTemplate;
    private final SnowFlakeUtil snowFlakeUtil;

    @Override
    public void deleteSubComment(String fid, String id) {
        final Query query = Query.query(
                Criteria.where(SubComment.Fields.subCommentId).is(id)
        );
        mongoTemplate.remove(query, SubComment.class);
    }

    @Override
    public void insertSubComment(String fId, String reply, String content, String nickname, String userImage, String username) {
        final SubComment subComment = new SubComment();
        subComment.setFId(fId);
        subComment.setSubCommentId(String.valueOf(snowFlakeUtil.nextId()));
        subComment.setTo(reply);
        subComment.setLikes(0L);
        subComment.setUsername(username);
        subComment.setContent(content);
        subComment.setCreateTime(String.valueOf(System.currentTimeMillis()));
        subComment.setNickname(nickname);
        subComment.setUserImage(userImage);
        mongoTemplate.insert(subComment);
    }

    @Override
    public List<SubComment> findAll(String fId, Integer pageSize, Integer begin) {
        //从skip开始 返回limit页
        final Query limitQuery = new Query();
        limitQuery
                .with(Sort.by(Sort.Order.asc(SubComment.Fields.createTime)));
        limitQuery.addCriteria(
                Criteria.where(SubComment.Fields.fId).is(fId)
        );
        return mongoTemplate.find(limitQuery, SubComment.class);
    }

    @Override
    public long addLike(String id) {
        final SubComment subComment = this.findBySubCommentId(id);

        final Query query = Query.query(
                Criteria.where(SubComment.Fields.subCommentId).is(id)
        );
        Update update = new Update();
        assert subComment != null;
        update.inc(SubComment.Fields.likes, 1);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, SubComment.class);
        return updateResult.getModifiedCount();
    }

    @Override
    public long reduceLike(String id) {
        final SubComment subComment = this.findBySubCommentId(id);

        Query query = Query.query(
                Criteria.where(SubComment.Fields.subCommentId).is(id)
        );
        assert subComment != null;
        if(subComment.getLikes() == 0L){
            return -1;
        }
        final Update update = new Update();
        update.inc(SubComment.Fields.likes, -1);
        final UpdateResult updateResult = mongoTemplate.updateFirst(query, update, SubComment.class);
        return updateResult.getModifiedCount();
    }

    public SubComment findBySubCommentId(String subCommentId) {
        Query query = Query.query(
                Criteria.where(SubComment.Fields.subCommentId).is(subCommentId)
        );
        return mongoTemplate.findOne(query, SubComment.class);
    }

//    @Override
//    public SubComment findByUsername(String username) {
//        Query query = Query.query(
//                Criteria.where(SubComment.Fields.username).is(username).and(Sub)
//        );
//        return mongoTemplate.findOne(query, SubComment.class);
//    }

    @Override
    public void deleteAllSubComment(String fId) {
        Query query = Query.query(
                Criteria.where(SubComment.Fields.fId).is(fId)
        );
        mongoTemplate.remove(query, SubComment.class);
    }

}
