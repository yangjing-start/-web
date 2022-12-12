package com.lt.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.model.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Lhz
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    void insertUser(User user);

}
