package com.yy.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean("orderExchange")
    public DirectExchange orderDirectExchange() {
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_ORDER);
    }

    @Bean("orderQueue")
    public Queue orderQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_ORDER).build();
    }

    @Bean
    public Binding OBinding(@Qualifier("orderQueue") Queue orderQueue,
                           @Qualifier("orderExchange")DirectExchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange).with(MqConst.ROUTING_ORDER);
    }

    @Bean("msmDirectExchange")
    public DirectExchange msmDirectExchange() {
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_MSM);
    }

    @Bean("msmQueue")
    public Queue msmQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_MSM_ITEM).build();
    }


    @Bean
    public Binding MBinding(@Qualifier("msmQueue") Queue msmQueue,
                           @Qualifier("msmDirectExchange")DirectExchange msmDirectExchange) {
        return BindingBuilder.bind(msmQueue).to(msmDirectExchange).with(MqConst.ROUTING_MSM_ITEM);
    }

    @Bean("TaskDirectExchange")
    public DirectExchange TaskDirectExchange() {
        return new DirectExchange(MqConst.EXCHANGE_DIRECT_TASK);
    }

    @Bean("TaskQueue")
    public Queue TaskQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_TASK_8).build();
    }
    @Bean
    public Binding TaskBinding(@Qualifier("TaskDirectExchange")DirectExchange taskDirectExchange,
                               @Qualifier("TaskQueue")Queue taskQueue) {
        return BindingBuilder.bind(taskQueue).to(taskDirectExchange).with(MqConst.ROUTING_TASK_8);
    }
}