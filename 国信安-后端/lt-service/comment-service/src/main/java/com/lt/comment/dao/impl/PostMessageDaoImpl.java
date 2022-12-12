package com.lt.comment.dao.impl;

import com.lt.comment.dao.PostMessageDao;
import com.lt.model.user.PostMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lhz
 */
@Repository
@RequiredArgsConstructor
public class PostMessageDaoImpl implements PostMessageDao {

    private final MongoTemplate mongoTemplate;

    @Override
    public void insertPostMessage(PostMessage postMessage) {
        mongoTemplate.insert(postMessage);
    }

    @Override
    public void deletePostMessage(String username) {
        final Query query = Query.query(
                Criteria.where(PostMessage.Fields.selfId).is(username)
        );
        mongoTemplate.remove(query, PostMessage.class);
    }

    @Override
    public List<PostMessage> getPostMessages(String username) {
        final Query query = Query.query(
                Criteria.where(PostMessage.Fields.selfId).is(username)
        );
        return mongoTemplate.find(query, PostMessage.class);
    }
}
