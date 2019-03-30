package com.huarui.mq;

import com.huarui.config.RabbitMQConfig;
import com.huarui.model.XcTask;
import com.huarui.service.LearningService;
import com.huarui.service.TaskService;
import com.huarui.util.JsonUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 任务类
 * Created by Shinelon on 2019/3/28.
 */
@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
    * 接收选课任务
    */
    @RabbitListener(queues = {RabbitMQConfig.LEARNING_ADD_QUEUE})
    public void receiveChoosecourseTask(XcTask xcTask, Message message, Channel channel) throws IOException {
        LOGGER.info("receive choose course task,taskId:{}",xcTask.getId());
        //接收到 的消息id
        String id = xcTask.getId();
        //添加选课
        try {
            String requestBody = xcTask.getRequestBody();
            Map map = JsonUtils.parseObject(requestBody, Map.class);
            String orderNumber = (String) map.get("orderNumber");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(map.get("startTime")!=null){
                startTime =dateFormat.parse(map.get("startTime").toString());
            }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse(map.get("endTime").toString());
            }
            //添加选课
            Integer count= learningService.addcourse(orderNumber,startTime, endTime,xcTask);
            //选课成功发送响应消息
            if(count>0){
                //发送响应消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.LEARNING_EXCHANGE, RabbitMQConfig.LEARNING_ADD_FINISH__KEY, xcTask );
                LOGGER.info("send finish choose course taskId:{}",id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("send finish choose course taskId:{}", id);
        }
    }


}
