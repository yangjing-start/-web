//package com.lt;
//
//import com.lt.match.config.RabbitMqConfig;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class ProducerTest {
//
//    @Autowired
//    public RabbitTemplate rabbitTemplate;
//
//    @Test
//    public void testSend(){
//        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "lt.lhz", "Hello !!!");
//    }
//}
