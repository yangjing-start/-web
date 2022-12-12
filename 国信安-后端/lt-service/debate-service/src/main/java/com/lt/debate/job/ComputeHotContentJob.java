package com.lt.debate.job;

import com.lt.debate.service.DebateContentService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author WanXin
 * @Date 2022/12/2
 */
@Component
@Slf4j
public class ComputeHotContentJob {

    @Autowired
    private DebateContentService debateContentService;

    @XxlJob("computeHotContentJob")
    public void handle() {
        log.info("热文章分值计算调度任务开始执行...");
        debateContentService.computeHotArticle();
        log.info("热文章分值计算调度任务结束...");
    }
}
