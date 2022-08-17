package com.yy.order.listener;

import com.rabbitmq.client.Channel;
import com.yy.order.service.IOrderInfoService;
import com.yy.rabbitmq.MqConst;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author ice
 * @date 2022/8/17 10:03
 */
@Component
@Log4j2
public class OrderReceiver {
    @Autowired
    private IOrderInfoService orderInfoService;
    // 预约提醒
    @RabbitListener(queues = MqConst.QUEUE_TASK_8)
    public void patientTips(Message message, Channel channel) {
        String time = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("========="+time+"=====================");
        orderInfoService.patientTips(time);
    }
}
