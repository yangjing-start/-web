package com.lt.model.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Lhz
 */
@Data
@TableName("user_product")
@FieldNameConstants
@ToString
public class UserProduct {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;
    /**
     * 用户名
     */
    public String username;
    /**
     * 地址
     */
    public String address;
    /**
     * 关注
     */
    public Integer follows;
    /**
     * 粉丝
     */
    public Integer fans;

    /**
     * 帖子
     */
    public Integer posts;
    /**
     * 用户头像
     */
    public String userImage;
    /**
     * 昵称
     */
    public String nickname;//
    /**
     * 性
     */
    public Integer sex;//
    /**
     * 简介
     */
    public String synopsis;//
    /**
     * 电子邮件
     */
    public String email;//
    /**
     * 电话
     */
    public String phone;//
}
