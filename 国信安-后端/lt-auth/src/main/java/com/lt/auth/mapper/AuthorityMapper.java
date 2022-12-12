package com.lt.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lt.model.user.Authority;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author Lhz
 */
@Mapper
public interface AuthorityMapper extends BaseMapper<Authority> {
//
//    @Select("SELECT r.id, r.name roleName" +
//            "FROM authority r, user_auth ur " +
//            "WHERE r.id=ur.rid AND ur.uid=#{username}")
//    List<Authority> findByUid(String username);
}
