package com.lt.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.model.user.UserAuthority;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Lhz
 */

@Mapper
public interface UserAuthorityMapper extends BaseMapper<UserAuthority> {

    int insertUserAuthority(UserAuthority entity);

}
