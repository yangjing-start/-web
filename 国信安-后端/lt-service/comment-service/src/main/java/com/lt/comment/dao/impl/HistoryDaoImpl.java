package com.lt.comment.dao.impl;

import com.lt.comment.dao.HistoryDao;
import com.lt.model.comment.History;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Lhz
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class HistoryDaoImpl implements HistoryDao {

    private final MongoTemplate mongoTemplate;

    @Override
    public void insertHistory(String username, String debateId, String debateNickname, String userImage, String title) {
        final History history = new History();
        history.setUsername(username);
        history.setTitle(title);
        history.setDebateId(debateId);
        history.setDebateNickname(debateNickname);
        history.setUserImage(userImage);
        history.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mongoTemplate.insert(history);
    }

    @Override
    public void deleteHistory(String debateId) {
        final Query query = Query.query(
                Criteria.where("debateId").is(debateId)
        );
        mongoTemplate.remove(query, History.class);
    }

    @Override
    public List<History> findAllHistory(String username, Integer pageSize, Integer beginPage) {
        final Query limitQuery = new Query();
        limitQuery.with(Sort.by(Sort.Order.desc("createTime")));

        limitQuery.addCriteria(
                Criteria.where("username").is(username)
        );
        return mongoTemplate.find(limitQuery, History.class);
    }

    @Override
    public Boolean findHistory(String username, String debateId) {
        final Query limitQuery = new Query();
        limitQuery.addCriteria(
                Criteria.where("username").is(username)
        );
        limitQuery.addCriteria(
                Criteria.where("debateId").is(debateId)
        );
        List<History> histories = mongoTemplate.find(limitQuery, History.class);

        return histories.size() == 0;
    }
}
