package com.geology.user.common.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Date:2024/9/5
 * author:zmh
 * description:RabbitMQ配置类
 **/

@Configuration
public class RabbitMQConfig {

    // 声明一个处理公共聊天的队列
    @Bean
    public Queue chatQueue() {
        return new Queue("chatQueue", true);
    }

    // 处理历史消息存储的队列
    @Bean
    public Queue saveQueue() {
        return new Queue("saveQueue", true);
    }

    // 维护接收者信息队列
    @Bean
    public Queue UpdateReceiverQueue() {
        return new Queue("update-receiver-queue", true);
    }

    // 配置消息转换器为 Jackson2JsonMessageConverter，实现复杂对象发送的支持
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}
