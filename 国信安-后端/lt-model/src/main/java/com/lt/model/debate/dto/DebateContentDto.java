package com.lt.model.debate.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author WanXin
 * @Date 2022/11/18
 */
@Data
public class DebateContentDto {

    private String content;

    private String summary;

    private String title;

    private String username;

    private List<String> url;
}
