package com.lt.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Lhz
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "lt.oss")
public class OssTemplate {

    private String accessKey;
    private String secret;
    private String bucketName;
    private String endpoint;
    private String url;

    public void delete(String fileName) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secret);

        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Oss删除文件出错");
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    /**
     * 文件上传
     * 1：文件名称
     * 2：输入流
     */
    public String upload(String filename, InputStream is) {
        //3、拼写图片路径
        filename = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/" + UUID.randomUUID() + filename + ".jpg";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, secret);

        // 填写Byte数组。
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(bucketName, filename, is);

        // 关闭OSSClient。
        ossClient.shutdown();

        return url + "/" + filename;
    }
}

