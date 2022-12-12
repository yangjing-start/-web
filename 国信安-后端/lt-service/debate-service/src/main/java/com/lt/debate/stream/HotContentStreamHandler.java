package com.lt.debate.stream;

import com.alibaba.fastjson.JSON;
import com.lt.model.debate.common.HotContentConstants;
import com.lt.model.debate.dto.ContentVisitStreamMess;
import com.lt.model.debate.dto.UpdateContentMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @Author WanXin
 * @Date 2022/11/29
 */
@Configuration
@Slf4j
public class HotContentStreamHandler {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream(HotContentConstants.HOT_CONTENT_SCORE_TOPIC);
        stream.map((key, value) -> {
            UpdateContentMess mess = JSON.parseObject(value, UpdateContentMess.class);
            return new KeyValue<>(mess.getContentId().toString(), mess.getType().name() + ":" + mess.getAdd());
        })
                .groupBy((key, value) -> key)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .aggregate(() -> "COMMENT:0,LIKES:0,VIEWS:0", (key, value, aggValue) -> {
                    if (StringUtils.isBlank(value)) {
                        return aggValue;
                    }
                    String[] aggAry = aggValue.split(",");
                    int lik = 0, com = 0, vie = 0;
                    for (String agg : aggAry) {
                        String[] split = agg.split(":");
                        switch (UpdateContentMess.UpdateContentType.valueOf(split[0])) {
                            case LIKES:
                                lik = Integer.parseInt(split[1]);
                                break;
                            case VIEWS:
                                vie = Integer.parseInt(split[1]);
                                break;
                            case COMMENT:
                                com = Integer.parseInt(split[1]);
                                break;
                        }
                    }

                    String[] valAry = value.split(":");
                    switch (UpdateContentMess.UpdateContentType.valueOf(valAry[0])) {
                        case COMMENT:
                            com += Integer.parseInt(valAry[1]);
                            break;
                        case LIKES:
                            lik += Integer.parseInt(valAry[1]);
                            break;
                        case VIEWS:
                            vie += Integer.parseInt(valAry[1]);
                            break;
                    }

                    String format = String.format("COMMENT:%d,LIKES:%d,VIEWS:%d", com, lik, vie);
                    return format;
                }, Materialized.as("hot-content-stream-count-001"))
                .toStream()
                .map((key, value) -> new KeyValue<>(key.key().toString(), formatObj(key.key().toString(), value)))
                .to(HotContentConstants.HOT_CONTENT_INCR_HANDLE_TOPIC);

        return stream;
    }

    public String formatObj(String id, String value) {
        ContentVisitStreamMess contentVisitStreamMess = new ContentVisitStreamMess();
        contentVisitStreamMess.setContentId(Long.valueOf(id));
        // "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0"
        String[] valAry = value.split(",");
        for (String s : valAry) {
            String[] split = s.split(":");
            switch (UpdateContentMess.UpdateContentType.valueOf(split[0])) {
                case COMMENT:
                    contentVisitStreamMess.setComment(Integer.parseInt(split[1]));
                    break;
                case LIKES:
                    contentVisitStreamMess.setLike(Integer.parseInt(split[1]));
                    break;
                case VIEWS:
                    contentVisitStreamMess.setView(Integer.parseInt(split[1]));
                    break;
            }
        }
        log.info("聚合消息处理之后的结果为:{}",JSON.toJSONString(contentVisitStreamMess));
//        kafkaTemplate.send(HotContentConstants.HOT_CONTENT_INCR_HANDLE_TOPIC, JSON.toJSONString(contentVisitStreamMess));
        return JSON.toJSONString(contentVisitStreamMess);
    }
}
