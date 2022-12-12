package com.lt.detail.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lt.detail.mapper.FansPostsMapper;
import com.lt.detail.mapper.UserProductMapper;
import com.lt.feign.comment.ICommentClient;
import com.lt.model.mq.MQPostMessage;
import com.lt.model.user.FansPosts;
import com.lt.model.user.UserProduct;
import com.lt.utils.Builder;
import com.lt.utils.JsonUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Lhz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FansPostsService {
    private final FansPostsMapper fansPostsMapper;
    private final UserProductMapper userProductMapper;
    private final TransactionTemplate transactionTemplate;
    private final RabbitTemplate rabbitTemplate;
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2, 4,
            300,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10),
            r -> new Thread(r, "FansPostsService"),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Transactional(rollbackFor = Exception.class)
    public int updateFans(final String postUsername, final String selfUserName, final String nickName, final String userImage) {
        final FansPosts fansPosts = new FansPosts();
        fansPosts.setFansId(selfUserName);
        fansPosts.setPostsId(postUsername);

        final UpdateWrapper<UserProduct> queryWrapperFans = new UpdateWrapper<>();
        queryWrapperFans.eq(UserProduct.Fields.username, postUsername);
        queryWrapperFans.setSql("fans = fans + 1");

        final UpdateWrapper<UserProduct> queryWrapperPost = new UpdateWrapper<>();
        queryWrapperPost.eq(UserProduct.Fields.username, selfUserName);
        queryWrapperPost.setSql("follows = follows + 1");

        final MQPostMessage mqPostMessage = Builder.builder(MQPostMessage::new)
                .with(MQPostMessage::setSelfUsername, postUsername)
                .with(MQPostMessage::setOtherNickname, nickName)
                .with(MQPostMessage::setOtherUserImage, userImage)
                .with(MQPostMessage::setOtherUsername, selfUserName).build();
        rabbitTemplate.convertAndSend("comment.topic", "comment.post", Objects.requireNonNull(JsonUtils.toString(mqPostMessage)));

        userProductMapper.update(null, queryWrapperFans);

        fansPostsMapper.insert(fansPosts);

        userProductMapper.update(null, queryWrapperPost);

        return 1;
    }
    @Transactional(rollbackFor = Exception.class)
    public int reduceFans(final String username, final String selfUserName) {
        final LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FansPosts::getFansId, selfUserName);

        final UpdateWrapper<UserProduct> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq(UserProduct.Fields.username, username);
        queryWrapper.setSql("fans = fans - 1");

        final UpdateWrapper<UserProduct> queryWrapper2 = new UpdateWrapper<>();
        queryWrapper2.eq(UserProduct.Fields.username, selfUserName);
        queryWrapper2.setSql("follows = follows - 1");

        transactionTemplate.execute(transactionStatus -> {
            userProductMapper.update(null, queryWrapper);
            userProductMapper.update(null, queryWrapper2);
            fansPostsMapper.delete(lambdaQueryWrapper);
            return null;
        });

        return 1;
    }


    public boolean checkFriend(final String username, final String selfUserName) {
        final LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FansPosts::getFansId, selfUserName).eq(FansPosts::getPostsId, username);
        final FansPosts fansPosts = fansPostsMapper.selectOne(lambdaQueryWrapper);
        return ObjectUtils.isEmpty(fansPosts);
    }

    public Boolean judgePost(final String selfUsername, final String otherUsername){
        LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FansPosts::getFansId, selfUsername);
        List<FansPosts> fansPosts = fansPostsMapper.selectList(lambdaQueryWrapper);
        for(FansPosts fansPosts1 : fansPosts){
            if(fansPosts1.getPostsId().equals(otherUsername)){
                return true;
            }
        }
        return false;
    }

}
