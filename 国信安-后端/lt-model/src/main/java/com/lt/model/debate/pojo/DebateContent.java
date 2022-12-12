package com.lt.model.debate.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author WanXin
 * @Date 2022/11/17
 */
@Data
@TableName("content")
@NoArgsConstructor
public class DebateContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("owner_id")
    private Integer ownerId;

    @TableField("info_id")
    private Integer infoId;

    private Integer score;

    private Integer state;

    @TableField("create_time")
    private Date createTime;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 标题
     */
    private String title;

    private Integer likes;

    @TableField("kind_id")
    private Integer kindId;

    /**
     * 评论数
     */
    private Integer remarks;

    private Integer views;

}
