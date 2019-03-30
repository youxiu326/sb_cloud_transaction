package com.huarui.mq;

import com.huarui.config.RabbitMQConfig;
import com.huarui.model.XcTask;
import com.huarui.service.TaskService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private TaskService taskService;

    //每隔1分钟扫描消息表，向mq发送消息 60000
    @Scheduled(fixedDelay = 60000)
    public void sendChoosecourseTask() {
        //取出当前时间1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE, -1);
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);
        //遍历任务列表
        for (XcTask xcTask : taskList) {
            //任务id
            String taskId = xcTask.getId();
            //版本号
            Integer version = xcTask.getVersion();
            //调用乐观锁方法校验任务是否可以执行
            if(taskService.getTask(taskId, version)>0) {
                //发送选课消息
                taskService.publish(xcTask.getId(), xcTask.getMqExchange(), xcTask.getMqRoutingkey());
                LOGGER.info("send choose course task id:{}", taskId);
            }
        }
    }

    //订单服务接收MQ完成选课的消息，将任务从当前任务表删除，将完成的任务添加到完成任务表

    /**
     * 接收选课响应结果
     */
    @RabbitListener(queues = {RabbitMQConfig.LEARNING_ADD_FINISH_QUEUE})
    public void receiveFinishChoosecourseTask(XcTask task, Message message, Channel channel) throws
            IOException {
        LOGGER.info("receiveChoosecourseTask...{}",task.getId());
        //接收到 的消息id
        String id = task.getId();
        //删除任务，添加历史任务
        taskService.finishTask(id);
    }

}
