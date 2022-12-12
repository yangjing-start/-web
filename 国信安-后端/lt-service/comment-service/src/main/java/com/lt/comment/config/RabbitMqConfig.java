package com.lt.comment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Lhz
 */
@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "comment.topic";

    public static final String DELETE_COMMENT_QUEUE_NAME = "comment.deleteCommentQueue";
    public static final String DELETE_SUB_COMMENT_QUEUE_NAME = "comment.deleteSubCommentQueue";

    public static final String DELETE_COMMENT_ROUTING_KEY = "comment.delete";
    public static final String DELETE_SUB_COMMENT_ROUTING_KEY = "subComment.delete";

    public static final String POST_MESSAGE_QUEUE_NAME = "comment.postQueue";
    public static final String POST_MESSAGE_ROUTING_KEY = "comment.post";

    public static final String POST_MESSAGE_DELETE_QUEUE_NAME = "comment.deletePostQueue";
    public static final String POST_MESSAGE_DELETE_ROUTING_KEY = "comment.deletePost";
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
    @Bean("deleteCommentQueue")
    public Queue registryQueue(){
        return QueueBuilder.durable(DELETE_COMMENT_QUEUE_NAME).build();
    }
    /**
     * 队列
     * @return Queue
     */
    @Bean("deleteSubCommentQueue")
    public Queue deleteQueue(){
        return QueueBuilder.durable(DELETE_SUB_COMMENT_QUEUE_NAME).build();
    }
    /**
     * 关注消息队列
     * @return Queue
     */
    @Bean("deletePostQueue")
    public Queue deletePostQueue(){
        return QueueBuilder.durable(POST_MESSAGE_DELETE_QUEUE_NAME).build();
    }
    /**
     * 关注消息队列
     * @return Queue
     */
    @Bean("postMessageQueue")
    public Queue postQueue(){
        return QueueBuilder.durable(POST_MESSAGE_QUEUE_NAME).build();
    }
    /**
     *  队列与交换机的绑定关系 Binding
     *  指定交换机 和 队列
     *  设置 Routing Key
     */

    @Bean
    public Binding bindQueueExchange(@Qualifier("deleteCommentQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELETE_COMMENT_ROUTING_KEY).noargs();
    }
    @Bean
    public Binding bindQueueExchange2(@Qualifier("deleteSubCommentQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DELETE_SUB_COMMENT_ROUTING_KEY).noargs();
    }
    @Bean
    public Binding bindQueueExchange3(@Qualifier("postMessageQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(POST_MESSAGE_ROUTING_KEY).noargs();
    }
    @Bean
    public Binding bindQueueExchange4(@Qualifier("deletePostQueue") Queue queue, @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(POST_MESSAGE_DELETE_ROUTING_KEY).noargs();
    }
}
