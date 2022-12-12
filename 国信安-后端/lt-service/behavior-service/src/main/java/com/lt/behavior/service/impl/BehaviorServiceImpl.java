package com.lt.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.lt.behavior.service.BehaviorService;
import com.lt.model.debate.common.HotContentConstants;
import com.lt.model.debate.dto.BehaviorConstants;
import com.lt.model.debate.dto.CommentBehaviorDto;
import com.lt.model.debate.dto.ReadBehaviorDto;
import com.lt.model.debate.dto.UpdateContentMess;
import com.lt.model.response.AppHttpCodeEnum;
import com.lt.model.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author WanXin
 * @Date 2022/11/27
 */
@Service
@Slf4j
public class BehaviorServiceImpl implements BehaviorService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Response like(Map map) {
        String contentId = (String) map.get("contentId");
        String operation = (String) map.get("operation"); // 0 保存  1 删除
        String userId = (String) map.get("username");

        if (StringUtils.isBlank(contentId) || StringUtils.isBlank(userId) || checkParam(operation)) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        UpdateContentMess mess = new UpdateContentMess();
        mess.setContentId(Long.valueOf(contentId));
        mess.setType(UpdateContentMess.UpdateContentType.LIKES);

        Integer op = Integer.valueOf(operation);
        if (op == 0) {
            Object obj = stringRedisTemplate.opsForHash().get(BehaviorConstants.LIKE_BEHAVIOR + contentId, userId);
            if (obj != null) {
                return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点赞");
            }

            stringRedisTemplate.opsForHash().put(BehaviorConstants.LIKE_BEHAVIOR + contentId, userId,
                    JSON.toJSONString(map));
            mess.setAdd(1);
        } else {
            Object obj = stringRedisTemplate.opsForHash().get(BehaviorConstants.LIKE_BEHAVIOR + contentId, userId);
            if (obj == null) {
                return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "未点赞，不能取消！");
            }

            stringRedisTemplate.opsForHash().delete(BehaviorConstants.LIKE_BEHAVIOR + contentId, userId);
            mess.setAdd(-1);
        }

        kafkaTemplate.send(HotContentConstants.HOT_CONTENT_SCORE_TOPIC, JSON.toJSONString(mess));

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public Response read(ReadBehaviorDto dto) {
        if (dto == null || dto.getContentId() == null) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String readBehaviorJson = (String) stringRedisTemplate.opsForHash()
                .get(BehaviorConstants.READ_BEHAVIOR + dto.getContentId(), dto.getUserId());
        if (StringUtils.isNotBlank(readBehaviorJson)) {
            ReadBehaviorDto readBehaviorDto = JSON.parseObject(readBehaviorJson, ReadBehaviorDto.class);
            dto.setCount((short) (readBehaviorDto.getCount() + dto.getCount()));
        }

        stringRedisTemplate.opsForHash().put(BehaviorConstants.READ_BEHAVIOR + dto.getContentId(), dto.getUserId(),
                JSON.toJSONString(dto));

        UpdateContentMess mess = new UpdateContentMess();
        mess.setContentId(Long.valueOf(dto.getContentId()));
        mess.setType(UpdateContentMess.UpdateContentType.VIEWS);
        mess.setAdd(1);
        kafkaTemplate.send(HotContentConstants.HOT_CONTENT_SCORE_TOPIC, JSON.toJSONString(mess));

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public Response comment(CommentBehaviorDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getContentId()) || checkParam(dto.getOp().toString())) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        UpdateContentMess mess = new UpdateContentMess();
        mess.setContentId(Long.valueOf(dto.getContentId()));
        mess.setType(UpdateContentMess.UpdateContentType.COMMENT);

        String json = (String) stringRedisTemplate.opsForHash()
                .get(BehaviorConstants.COMMENT_BEHAVIOR + dto.getContentId(), dto.getUsername());
        // 新增评论
        if (dto.getOp() == 0) {
            dto.setCount(1);
            if (StringUtils.isNotBlank(json)) {
                CommentBehaviorDto commentBehaviorDto = JSON.parseObject(json, CommentBehaviorDto.class);
                dto.setCount(commentBehaviorDto.getCount() + 1);
            }

            stringRedisTemplate.opsForHash()
                    .put(BehaviorConstants.COMMENT_BEHAVIOR + dto.getContentId(),
                            dto.getUsername(), JSON.toJSONString(dto));
            mess.setAdd(1);
        } else { // 删除评论
            if (StringUtils.isBlank(json)) {
                return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID, "你没有评论过！无法删除评论！");
            }

            CommentBehaviorDto commentBehaviorDto = JSON.parseObject(json, CommentBehaviorDto.class);
            // 如果只有一条评论，则直接删除记录
            if (commentBehaviorDto.getCount() <= 1) {
                stringRedisTemplate.opsForHash()
                        .delete(BehaviorConstants.COMMENT_BEHAVIOR + dto.getContentId(), dto.getUsername());
            } else {
                dto.setCount(commentBehaviorDto.getCount() - 1);
                stringRedisTemplate.opsForHash()
                        .put(BehaviorConstants.COMMENT_BEHAVIOR + dto.getContentId(),
                                dto.getUsername(), JSON.toJSONString(dto));
            }
            mess.setAdd(-1);
        }

        kafkaTemplate.send(HotContentConstants.HOT_CONTENT_SCORE_TOPIC, JSON.toJSONString(mess));

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 检查参数
     * @return
     */
    private boolean checkParam(String operationStr) {
        int operation = Integer.parseInt(operationStr);
        return operation > 1 || operation < 0;
    }
}
