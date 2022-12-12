package com.lt.detail.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lt.detail.mapper.FansPostsMapper;
import com.lt.detail.mapper.UserProductMapper;
import com.lt.model.user.FansPosts;
import com.lt.model.user.UserProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lhz
 */
@Service
@RequiredArgsConstructor
public class FriendService {

    private final FansPostsMapper fansPostsMapper;
    private final UserProductMapper userMapper;

    public List<UserProduct> findFriends(String username) {
        Map<String, Boolean> map = new HashMap<>(1000);
        LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FansPosts::getPostsId, username);
        List<FansPosts> fansList = fansPostsMapper.selectList(lambdaQueryWrapper);
        List<String> fans = fansList.stream().map(FansPosts::getFansId).collect(Collectors.toList());
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(FansPosts::getFansId, username);
        List<FansPosts> postsList = fansPostsMapper.selectList(lambdaQueryWrapper);
        List<String> posts = postsList.stream().map(FansPosts::getPostsId).collect(Collectors.toList());
        List<UserProduct> friends = new ArrayList<>();
        for (String fan : fans) {
            map.put(fan, true);
        }
        LambdaQueryWrapper<UserProduct> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        for (String post : posts) {
            if (map.get(post) != null) {
                lambdaQueryWrapper3.clear();
                lambdaQueryWrapper3.eq(UserProduct::getUsername, post);
                UserProduct user = userMapper.selectOne(lambdaQueryWrapper3);
                friends.add(user);
            }
        }
        return friends;
    }

    public List<UserProduct> fans(String username){
        LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(FansPosts::getPostsId, username);
        List<FansPosts> fansList = fansPostsMapper.selectList(lambdaQueryWrapper);

        LambdaQueryWrapper<UserProduct> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        List<UserProduct> users = new ArrayList<>();
        for (FansPosts fansPosts: fansList) {
            lambdaQueryWrapper3.eq(UserProduct::getUsername, fansPosts.getFansId());
            UserProduct user = userMapper.selectOne(lambdaQueryWrapper3);
            users.add(user);
            lambdaQueryWrapper3.clear();
        }
        return users;
    }

    public List<UserProduct> follows(String username){
        LambdaQueryWrapper<FansPosts> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(FansPosts::getFansId, username);
        List<FansPosts> postsList = fansPostsMapper.selectList(lambdaQueryWrapper);

        LambdaQueryWrapper<UserProduct> lambdaQueryWrapper3 = new LambdaQueryWrapper<>();
        List<UserProduct> follows = new ArrayList<>();
        for(FansPosts fansPosts: postsList){
            lambdaQueryWrapper3.eq(UserProduct::getUsername,fansPosts.getPostsId());
            UserProduct userProduct = userMapper.selectOne(lambdaQueryWrapper3);
            follows.add(userProduct);
            lambdaQueryWrapper3.clear();
        }

        return follows;
    }
}
