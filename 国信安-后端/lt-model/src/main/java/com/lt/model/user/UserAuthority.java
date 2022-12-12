package com.lt.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;


/**
 * 用户角色
 *
 * @author Lhz
 * @date 2022/11/15
 */
@TableName("user_auth")
@ToString
@Data
public class UserAuthority {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;
    /**
     * 用户id
     */
    String uid;
    /**
     * 角色id
     */
    Integer aid;

    /**
     * 用户角色
     */
    public UserAuthority() {
    }

}
