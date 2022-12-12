package com.lt.detail.listener;

import com.google.gson.Gson;
import com.lt.detail.service.UserProductService;
import com.lt.model.mq.RegistrationMessage;
import com.lt.model.user.UserProduct;
import com.lt.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.lt.detail.config.RabbitMqConfig.DELETE_QUEUE_NAME;
import static com.lt.detail.config.RabbitMqConfig.REGISTRY_QUEUE_NAME;

/**
 * @author Lhz
 */
@Component
@RequiredArgsConstructor
public class AuthServiceListener {

    private final UserProductService userProductService;

    @RabbitListener(queues = REGISTRY_QUEUE_NAME)
    public void listenAuthRegistrationMessage(String registrationDTO){
        System.out.println("registrationMQ:" + registrationDTO);
        RegistrationMessage registrationMessage = JsonUtils.toBean(registrationDTO, RegistrationMessage.class);
        UserProduct userProduct = new UserProduct();
        userProduct.setAddress("空");
        userProduct.setFans(0);
        userProduct.setFollows(0);
        userProduct.setNickname(registrationMessage.nickname);
        userProduct.setPhone(null);
        userProduct.setUserImage("https://lhztest01.oss-cn-chengdu.aliyuncs.com/image-2022-09-27-5518cba0-3e44-11ed-83f5-8975dc697742.jpg");
        userProduct.setSex(null);
        userProduct.setPosts(0);
        userProduct.setSynopsis("无简介");
        userProduct.setEmail(registrationMessage.mail);
        userProduct.setUsername(registrationMessage.username);
        userProductService.insertUser(userProduct);
    }

    @RabbitListener(queues = DELETE_QUEUE_NAME)
    public void listenAuthDeleteUserMessage(String username){
        System.out.println("DELETE_QUEUE_NAME:" + username);
        userProductService.deleteUser(username);
    }
}
