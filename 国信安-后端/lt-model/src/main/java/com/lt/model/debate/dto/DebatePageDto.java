package com.lt.model.debate.dto;

import com.lt.model.debate.common.PageRequestDto;
import lombok.Data;

/**
 * @Author WanXin
 * @Date 2022/11/17
 */
@Data
public class DebatePageDto extends PageRequestDto {

    private String kindId;
    private String key;
}
