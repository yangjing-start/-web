package com.lt.debate.service;

import com.lt.model.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface MaterialService {

    /**
     * 保存素材
     * @param file
     * @param userId
     * @return
     */
    Response save(MultipartFile file, Integer userId);

    /**
     * 删除素材
     * @param map
     * @return
     */
    Response delete(Map map);

    /**
     * 获取全部素材
     * @param map
     * @return
     */
    Response getAll(Map map);
}
