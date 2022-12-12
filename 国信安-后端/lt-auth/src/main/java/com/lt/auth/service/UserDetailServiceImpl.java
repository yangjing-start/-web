package com.lt.auth.service;

import com.alibaba.nacos.common.utils.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.lt.auth.controller.dto.RegistrationDTO;
import com.lt.auth.mapper.*;
import com.lt.feign.userproduct.IUserProductClient;
import com.lt.model.mq.RegistrationMessage;
import com.lt.model.user.Authority;
import com.lt.model.user.User;
import com.lt.model.user.UserAuthority;
import com.lt.model.user.UserProduct;
import com.lt.utils.Builder;
import com.lt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lhz
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService, UserDetailsPasswordService {
    private final RabbitTemplate rabbitTemplate;
    private final UserCache userCache = new NullUserCache();
    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;
    private final UserAuthorityMapper userAuthorityMapper;
    private AuthenticationManager authenticationManager;
    private final RedissonClient redisClient;
    private static final String MAIL_REGEX = "^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$";

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (username.matches(MAIL_REGEX)) {
            lambdaQueryWrapper.eq(User::getEmail, username);
        } else {
            lambdaQueryWrapper.eq(User::getUsername, username);
        }
        User user = userMapper.selectOne(lambdaQueryWrapper);
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("没有找到用户");
        }
        final LambdaQueryWrapper<UserAuthority> userAuthorityQueryWrapper = new LambdaQueryWrapper<>();
        userAuthorityQueryWrapper.eq(UserAuthority::getUid, user.getUsername());
        final List<UserAuthority> userAuthorities = userAuthorityMapper.selectList(userAuthorityQueryWrapper);
        final LambdaQueryWrapper<Authority> authorityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        authorityLambdaQueryWrapper.eq(Authority::getId, userAuthorities.get(0).getAid());
        userAuthorities.remove(0);
        for(UserAuthority userAuthority : userAuthorities){
            authorityLambdaQueryWrapper.or();
            authorityLambdaQueryWrapper.eq(Authority::getId, userAuthority.getAid());
        }
        final List<Authority> authorities = authorityMapper.selectList(authorityLambdaQueryWrapper);
        user.setAuthorities(authorities);
        return user;
    }

    /**
     * 默认使用DelegatingPassword 默认使用相对最安全的加密
     *
     * @param user        用户名
     * @param newPassword 新的密码
     * @return UserDetails
     */



    public Pair<Boolean, String> registration(final RegistrationDTO dto) throws InterruptedException {
        final LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        final String email = dto.getMail();
        final String nickname = dto.getNickname();
        final String password = dto.getPassword();
        lambdaQueryWrapper.eq(User::getEmail, email);
        //Redisson分布式锁
        RLock lock = redisClient.getLock("registry");
        //重试时间为3秒 自动过期时间为十秒，
        boolean isLock = lock.tryLock(3, 10, TimeUnit.SECONDS);
        if(isLock){
            try {
                if (userMapper.selectOne(lambdaQueryWrapper) != null) {
                    return Pair.with(false, "");
                }
                while (true) {
                    final int temp = (int) ((Math.random() * 9 + 1) * 100000);
                    final String username = String.valueOf(temp);
                    lambdaQueryWrapper.eq(User::getUsername, username);
                    if (ObjectUtils.isEmpty(userMapper.selectOne(lambdaQueryWrapper))) {
                        final User user = new User();
                        user.setEmail(email);
                        user.setUsername(username);
                        user.setPassword("{noop}" + password);
                        user.setCreateTime(new Date());
                        userMapper.insert(user);
                        UserAuthority userAuthority = new UserAuthority();
                        userAuthority.setUid(username);
                        userAuthority.setAid(1);
                        userAuthorityMapper.insert(userAuthority);
                        UserAuthority userAuthority2 = new UserAuthority();
                        userAuthority2.setUid(username);
                        userAuthority2.setAid(2);
                        userAuthorityMapper.insert(userAuthority2);
                        RegistrationMessage registrationMessage = Builder.builder(RegistrationMessage::new)
                                .with(RegistrationMessage::setMail, email)
                                .with(RegistrationMessage::setUsername, username)
                                .with(RegistrationMessage::setNickname, nickname).build();
                        System.err.println(Objects.requireNonNull(JsonUtils.toString(registrationMessage)));
                        rabbitTemplate.convertAndSend("auth.topic","auth.registration.xxx", Objects.requireNonNull(JsonUtils.toString(registrationMessage)));
                        return Pair.with(true, username);
                    }
                }
            }finally {
                lock.unlock();
            }
        }
        return Pair.with(false, "超时");
    }

    /**
     * 修改用户敏感信息
     *
     * @param user
     * @return
     */
    public int updateUser(final User user) {
        this.validateUserDetails(user);
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, user.getUsername());
        final int res = userMapper.update(user, lambdaUpdateWrapper);
        if (this.getEnableAuthorities()) {
            this.deleteUserAuthorities(user.getUsername());
            this.insertUserAuthorities(user);
        }
        this.userCache.removeUserFromCache(user.getUsername());
        return res;
    }

    private void insertUserAuthorities(User user) {
        final String username = user.getUsername();
        for (GrantedAuthority auth : user.getAuthorities()) {
            final LambdaQueryWrapper<Authority> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Authority::getName, auth.getAuthority());
            final Authority authority = authorityMapper.selectOne(lambdaQueryWrapper);
            final UserAuthority userRole = new UserAuthority();
            userRole.setUid(username);
            userRole.setAid(authority.getId());
            userAuthorityMapper.insert(userRole);
        }
    }

    private void deleteUserAuthorities(final String username) {
        final LambdaQueryWrapper<UserAuthority> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserAuthority::getUid, username);
        this.userAuthorityMapper.delete(lambdaQueryWrapper);
    }

    private void validateUserDetails(final User user) {
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        this.validateAuthorities(user.getAuthorities());
    }

    private void validateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }

    }

    protected boolean getEnableAuthorities() {
        return true;
    }

    public void deleteUser(final String username) {
        rabbitTemplate.convertAndSend("auth.topic","auth.delete.xxx", username);
        if (this.getEnableAuthorities()) {
            this.deleteUserAuthorities(username);
        }
        final LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername, username);
        userMapper.delete(lambdaQueryWrapper);

        this.userCache.removeUserFromCache(username);
    }

    public void changePassword(String oldPassword, String newPassword) throws AuthenticationException {
        final Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("Can't change password as no Authentication object found in context for current auth.");
        } else {
            final String username = currentUser.getName();
            System.out.println(username);
            if (this.authenticationManager != null) {
                System.out.println("Reauthenticating auth '" + username + "' for password change request.");
                this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
            } else {
                System.out.println("No authentication manager set. Password won't be re-checked.");
            }
            System.out.println("Changing password for auth '" + username + "'");
            final LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getUsername, currentUser.getName()).set(User::getPassword, "{noop}" + newPassword);
            userMapper.update(null, lambdaUpdateWrapper);
            SecurityContextHolder.getContext().setAuthentication(this.createNewAuthentication(currentUser));
            this.userCache.removeUserFromCache(username);
        }
    }

    protected Authentication createNewAuthentication(final Authentication currentAuth) {
        final UserDetails user = this.loadUserByUsername(currentAuth.getName());
        final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(user, (Object) null, user.getAuthorities());
        newAuthentication.setDetails(currentAuth.getDetails());
        return newAuthentication;
    }

    public boolean userExists(final String username) {
        final QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("username").eq("username", username);
        final List<User> users = userMapper.selectList(queryWrapper);
        if (users.size() > 1) {
            throw new IncorrectResultSizeDataAccessException("More than one auth found with name '" + username + "'", 1);
        } else {
            return users.size() == 1;
        }
    }

    public void setAuthenticationManager(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails updatePassword(final UserDetails user, final String newPassword) {
        final LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getUsername, user.getUsername()).set(User::getPassword, newPassword);
        int updatePassword = userMapper.update(null, lambdaUpdateWrapper);
        if (updatePassword == 1) {
            ((User) user).setPassword(newPassword);
        }
        return user;
    }
}
