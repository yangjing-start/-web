package com.lt.detail.controller.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


/**
 * @author Lhz
 */
@Data
@ToString
@Builder
public class OtherPageVO {
    private Integer follows;
    private String username;
    private Integer fans;
    private Integer posts;
    private Boolean followed;
    private String nickname;
    private String userImage;
    private String synopsis;
}
