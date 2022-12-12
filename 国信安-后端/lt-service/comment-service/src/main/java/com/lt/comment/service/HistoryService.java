package com.lt.comment.service;

import com.lt.comment.dao.impl.HistoryDaoImpl;
import com.lt.model.comment.History;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Lhz
 */

@RequiredArgsConstructor
@Service
public class HistoryService {

    private final HistoryDaoImpl historyDao;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public void insertHistory(final String username, final String debateId, final String debateNickname, final String userImage, String title) {
        if(historyDao.findHistory(username, debateId)){
            historyDao.insertHistory(username, debateId, debateNickname, userImage, title);
        }
    }


    public void deleteHistory(final String debateId) {
        historyDao.deleteHistory(debateId);
    }

    public List<History> getHistory(final String username, final Integer pageSize, final Integer beginPage) {
        List<History> res = historyDao.findAllHistory(username, pageSize, beginPage);
        for(History history: res){
            long time = Long.parseLong(history.getCreateTime());
            String date = format.format(time);
            history.setCreateTime(date);
        }
        return res;
    }
}
