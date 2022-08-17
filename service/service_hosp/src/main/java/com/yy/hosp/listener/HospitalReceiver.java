package com.yy.hosp.listener;

import com.rabbitmq.client.Channel;
import com.yy.hosp.service.ScheduleService;
import com.yy.rabbitmq.MqConst;
import com.yy.rabbitmq.RabbitService;
import com.yy.util.exception.RException;
import com.yy.yygh.model.hosp.Schedule;
import com.yy.yygh.vo.msm.MsmVo;
import com.yy.yygh.vo.order.OrderMqVo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * rabbitMq监听器
 * @author ice
 * @date 2022/8/15 20:37
 */
@Component
public class HospitalReceiver {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER)
    public void receiver(OrderMqVo orderMqVo, Channel channel, Message message) {
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);

            MsmVo msmVo = orderMqVo.getMsmVo();
            boolean b =  scheduleService.update(orderMqVo);

            if (msmVo != null && b) {
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
            }

        } catch (IOException e) {
            throw new RException("发送失败");
        }
    }
}
