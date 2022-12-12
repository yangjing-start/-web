package com.lt.debate.listener;

import com.alibaba.fastjson.JSON;

import com.lt.debate.service.DebateContentService;
import com.lt.model.debate.common.HotContentConstants;
import com.lt.model.debate.dto.ContentVisitStreamMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Author WanXin
 * @Date 2022/11/30
 */
@Component
@Slf4j
public class ContentIncrHandlerListener {

    @Autowired
    private DebateContentService debateContentService;

    @KafkaListener(topics = HotContentConstants.HOT_CONTENT_INCR_HANDLE_TOPIC)
    public void onMessage(String mess) {
        if (StringUtils.isNotBlank(mess)) {
            log.info("listener:{}", mess);
            ContentVisitStreamMess contentVisitStreamMess = JSON.parseObject(mess, ContentVisitStreamMess.class);
            debateContentService.updateScore(contentVisitStreamMess);
        }
    }
}
