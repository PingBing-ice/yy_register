package com.yy.task.reminder;

import com.yy.rabbitmq.MqConst;
import com.yy.rabbitmq.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务
 *
 * @author ice
 * @date 2022/8/17 9:31
 */
@Component
public class ScheduledTask {
    @Autowired
    private RabbitService rabbitService;

    /**
     * 预约提醒
     */

    @Scheduled(cron = "0 0/3 * * * ?")
    public void reminderTask() {
        String time = "当前时间为: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,time);
        System.out.println(time);

    }
}
