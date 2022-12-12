package com.lt.model.debate.pojo;

import lombok.Data;

import java.util.List;

/**
 * @Author WanXin
 * @Date 2022/11/18
 */
@Data
public class DebateContentES extends DebateContent{

    private List<String> suggestion;
}
