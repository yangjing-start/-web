package com.lt.detail.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lt.detail.mapper.UserProductMapper;
import com.lt.model.user.UserProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lhz
 */
@Service
@RequiredArgsConstructor
public class UserProductService {

    private final UserProductMapper userProductMapper;
    private static final String PHONE_NUMBER_REGEX = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";

    public boolean deleteUser(String username){
        LambdaQueryWrapper<UserProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProduct::getUsername, username);
        int delete = userProductMapper.delete(queryWrapper);
        return delete == 1;
    }

    public UserProduct getUserDetail(String username){
        LambdaQueryWrapper<UserProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProduct::getUsername, username);

        return userProductMapper.selectOne(queryWrapper);
    }

    public int updateUserImage(String userImage, String username) {
        UpdateWrapper<UserProduct> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.set("user_image", userImage);
        return userProductMapper.update(null, queryWrapper);
    }

    public int update(String description, String value, String username) {
        UpdateWrapper<UserProduct> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq(UserProduct.Fields.username, username);
        if (Objects.equals(UserProduct.Fields.posts, description)) {
            queryWrapper.setSql("posts = posts - 1");
        } else if (Objects.equals(UserProduct.Fields.follows, description)) {
            queryWrapper.setSql("follows = follows + 1");
        } else if (Objects.equals(UserProduct.Fields.phone, description)) {
            if (value.matches(PHONE_NUMBER_REGEX)) {
                queryWrapper.set(description, value);
            } else {
                return 3;
            }
        } else if (Objects.equals(UserProduct.Fields.nickname, description) ||Objects.equals(UserProduct.Fields.sex, description) || Objects.equals(UserProduct.Fields.address, description) || Objects.equals(UserProduct.Fields.synopsis, description)) {
            queryWrapper.set(description, value);
        } else {
            return 4;
        }
        userProductMapper.update(null, queryWrapper);
        return 2;
    }

    public void insertUser(UserProduct userProduct) {
        userProductMapper.insert(userProduct);
    }

    public int updateUserPosts(String username) {
        UpdateWrapper<UserProduct> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.setSql("posts = posts + 1");
        return userProductMapper.update(null, queryWrapper);
    }
}
