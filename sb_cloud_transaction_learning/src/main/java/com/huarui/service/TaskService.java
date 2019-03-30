package com.huarui.service;/**
 * @Author lihui
 * @Date $ $
 */

import com.huarui.dao.XcTaskRepository;
import com.huarui.model.XcTask;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by Shinelon on 2019/3/28.
 */
@Service
public class TaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime, int n){
        //设置分页参数，取出前n 条记录
        Pageable pageable = new PageRequest(0, n);
        Page<XcTask> xcTasks = xcTaskRepository.findByUpdateTimeBefore(pageable,updateTime);
        return xcTasks.getContent();
    }

    //乐观锁方法校验任务
    @Transactional
    public int getTask(String taskId,int version){
        int i = xcTaskRepository.updateTaskVersion(taskId, version);
        return i;
    }

    /**
     * //发送消息
     * @param taskId 任务对象id
     * @param ex 交换机id
     * @param routingKey
     */
    @Transactional
    public void publish(String taskId, String ex, String routingKey){
        //查询任务
        Optional<XcTask> taskOptional = Optional.ofNullable(xcTaskRepository.findOne(taskId));
        if(taskOptional.isPresent()){
            XcTask xcTask = taskOptional.get();
            //String exchange, String routingKey, Object object
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间为当前时间
            xcTask.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask);
        }
    }


}
