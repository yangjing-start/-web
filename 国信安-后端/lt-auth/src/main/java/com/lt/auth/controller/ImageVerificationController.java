package com.lt.auth.controller;

import com.google.code.kaptcha.Producer;
import com.lt.model.response.Response;
import com.lt.utils.SnowFlakeUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;

/**
 * 图像验证控制器
 *
 * @author Lhz
 * @date 2022/11/15
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
public class ImageVerificationController {


    private final Producer producer;

    private final RedisTemplate<String, String> redisTemplate;

    private final SnowFlakeUtil snowFlakeUtil;

    /**
     * @author Lhz
     * @date 2022/11/15
     */
    @Data
    static class VcDTO {
        /**
         * base64
         */
        String base64;
        /**
         * 时间
         */
        String time;

        /**
         * vc dto
         *
         * @param result 结果
         * @param time   时间
         */
        public VcDTO(String result, String time) {
            this.base64 = result;
            this.time = time;
        }
    }

    /**
     * 获取验证码
     *
     * @return {@link Response}<{@link VcDTO}>
     * @throws IOException ioexception
     */
    @GetMapping("/vc.jpg")
    public Response<VcDTO> getVerifyCode() throws IOException {
        String text = producer.createText();
        long time = snowFlakeUtil.nextId();
        //验证码存入Redis
        redisTemplate.opsForValue().set(String.valueOf(time), text, Duration.ofMinutes(10));
        BufferedImage image = producer.createImage(text);
        FastByteArrayOutputStream fastByteArrayOutputStream = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", fastByteArrayOutputStream);
        String result = Base64.encodeBase64String(fastByteArrayOutputStream.toByteArray());
        return new Response<>(HttpStatus.OK.value(), "获取成功", new VcDTO(result, String.valueOf(time)));

    }
}
