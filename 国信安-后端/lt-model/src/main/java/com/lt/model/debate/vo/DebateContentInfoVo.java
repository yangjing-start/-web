package com.lt.model.debate.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author WanXin
 * @Date 2022/12/4
 */
@Data
public class DebateContentInfoVo {

    private Integer id;

    private String content;

    private String title;

    private List<String> image;

    private Boolean flag;
}
