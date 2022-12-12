package com.lt.model.user;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * @author Lhz
 */
@Data
@TableName("user")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class User implements UserDetails {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 启用
     */
    @Getter(value = AccessLevel.NONE)
    private Boolean enabled = true;
    /**
     * 账户不过期
     */
    @Getter(value = AccessLevel.NONE)
    private Boolean accountNonExpired = true;
    /**
     * 非锁定账户
     */
    @Getter(value = AccessLevel.NONE)
    private Boolean accountNonLocked = true;
    /**
     * 凭证不过期
     */
    @Getter(value = AccessLevel.NONE)
    private Boolean credentialsNonExpired = true;
    /**
     * 角色
     */
    @Getter(value = AccessLevel.NONE)
    private List<Authority> authorities;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 得到当局
     * 返回权限信息
     *
     * @return 用户详细信息
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 是账户非过期
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * 是账户非锁定
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * 是凭证不过期
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * 启用了
     *
     * @return boolean
     */
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
