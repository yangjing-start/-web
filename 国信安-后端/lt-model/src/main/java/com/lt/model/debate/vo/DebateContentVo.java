package com.lt.model.debate.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @Author WanXin
 * @Date 2022/12/4
 */
@Data
public class DebateContentVo {

    private Integer id;

    private Integer ownerId;

    private Integer infoId;

    private Date createTime;

    private String summary;

    private String title;

    private Integer likes;

    private Integer remarks;

    private Integer views;
}
