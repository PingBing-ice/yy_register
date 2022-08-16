package com.yy.msm.listener;

import com.rabbitmq.client.Channel;
import com.yy.msm.service.MsmService;
import com.yy.rabbitmq.MQConfig;
import com.yy.rabbitmq.MqConst;
import com.yy.util.exception.RException;
import com.yy.yygh.vo.msm.MsmVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ice
 * @date 2022/8/15 21:17
 */
@Component
public class MsmRabbitListener {
    @Autowired
    private MsmService msmService;

    @RabbitListener(queues = MqConst.QUEUE_MSM_ITEM)
    public void listener(MsmVo msmVo, Channel channel, Message message) {
        msmService.send(msmVo);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            throw new RException("发送短信接收失败");
        }
    }
}
