package com.lt.debate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.lt.debate.mapper.MaterialMapper;
import com.lt.debate.service.MaterialService;
import com.lt.model.debate.pojo.Material;
import com.lt.model.response.AppHttpCodeEnum;
import com.lt.model.response.Response;
import com.lt.utils.OssTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author WanXin
 * @Date 2022/11/23
 */
@Service
@Transactional
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private OssTemplate ossTemplate;

    @Override
    public Response save(MultipartFile file, Integer userId) {
        if (userId == null || file == null) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        String fileName = null;
        try {
            fileName = ossTemplate.upload(userId.toString(), file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Material material = new Material();
        material.setCreateTime(new Date());
        material.setImage(fileName);
        material.setOwnerId(userId);
        material.setState(1);

        materialMapper.insert(material);

        return Response.okResult(material);
    }

    @Override
    public Response delete(Map map) {
        Integer id = Integer.valueOf((String) map.get("id"));
        if (id == null) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        Material material = materialMapper.selectById(id);
        if (material == null) {
            return Response.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        material.setState(0);

        materialMapper.updateById(material);

        return Response.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public Response getAll(Map map) {
        Integer username = Integer.valueOf((String) map.get("username"));
        if (username == null) {
            return Response.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        LambdaQueryWrapper<Material> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Material::getOwnerId, username);
        lambdaQueryWrapper.eq(Material::getState, 1);
        lambdaQueryWrapper.orderByDesc(Material::getCreateTime);

        List<Material> materials = materialMapper.selectList(lambdaQueryWrapper);

        return Response.okResult(materials);
    }
}
