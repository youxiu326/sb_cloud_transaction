package com.huarui.service;

import com.alibaba.druid.util.StringUtils;
import com.huarui.dao.XcLearningCourseRepository;
import com.huarui.dao.XcTaskHisRepository;
import com.huarui.model.XcLearningCourse;
import com.huarui.model.XcTask;
import com.huarui.model.XcTaskHis;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class LearningService {

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private XcLearningCourseRepository xcLearningCourseRepository;

    //完成选课
    @Transactional(rollbackFor = Exception.class)
    public int addcourse(String orderNumber,Date startTime, Date endTime, XcTask xcTask){
        if (StringUtils.isEmpty(orderNumber)) {
            return 0;
        }
        if(xcTask == null || StringUtils.isEmpty(xcTask.getId())){
            return 0;
        }
        //查询历史任务
        Optional<XcTaskHis> optional = Optional.ofNullable(xcTaskHisRepository.findOne(xcTask.getId()));
        if(optional.isPresent()){
            return 0;
        }
        String userId = orderNumber.substring(0,6);
        String courseId = orderNumber.substring(6);
        XcLearningCourse xcLearningCourse =  xcLearningCourseRepository.findXcLearningCourseByUserIdAndCourseId(userId, courseId);
        if (xcLearningCourse == null) {//没有选课记录则添加
            xcLearningCourse = new XcLearningCourse();
            xcLearningCourse.setUserId(userId);
            xcLearningCourse.setCourseId(courseId);
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus(xcTask.getStatus());
            xcLearningCourseRepository.save(xcLearningCourse);
        } else {//有选课记录则更新日期
            xcLearningCourse.setStartTime(startTime);
            xcLearningCourse.setEndTime(endTime);
            xcLearningCourse.setStatus(xcTask.getStatus());
            xcLearningCourseRepository.save(xcLearningCourse);
        }
        //向历史任务表插入记录
        Optional<XcTaskHis> his = Optional.ofNullable(xcTaskHisRepository.findOne(xcTask.getId()));
        if(!his.isPresent()){
            //添加历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
        }
        return 1;
    }

}
