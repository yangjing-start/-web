package com.lt.detail.config;

import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lhz
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "auth.topic";
    public static final String REGISTRY_QUEUE_NAME = "auth.registryQueue";
    public static final String DELETE_QUEUE_NAME = "auth.deleteQueue";

    /**
     * 交换机
     * @return Exchange
     */
    @Bean(EXCHANGE_NAME)
    public Exchange ltExchange(){
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME)
                .durable(true)
                .build();
    }
    /**
     * 队列
     * @return Queue
     */
    @Bean("authRegistryQueue")
    public Queue registryQueue(){
        return QueueBuilder.durable(REGISTRY_QUEUE_NAME).build();
    }
    /**
     * 队列
     * @return Queue
     */
    @Bean("authDeleteQueue")
    public Queue deleteQueue(){
        return QueueBuilder.durable(DELETE_QUEUE_NAME).build();
    }

    /**
     *  队列与交换机的绑定关系 Binding
     *  指定交换机 和 队列
     *  设置 Routing Key
     */
    @Bean
    public Binding bindQueueExchange(@Qualifier("authRegistryQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("auth.registration.#").noargs();
    }
    @Bean
    public Binding bindQueueExchange2(@Qualifier("authDeleteQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("auth.delete.#").noargs();
    }
}
