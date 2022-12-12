package com.lt.model.debate.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MaterialDto {

    private MultipartFile multipartFile;

    private Integer username;
}
