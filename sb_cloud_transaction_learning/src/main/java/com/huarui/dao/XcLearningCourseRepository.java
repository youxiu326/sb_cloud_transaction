package com.huarui.dao;

import com.huarui.model.XcLearningCourse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcLearningCourseRepository extends JpaRepository<XcLearningCourse, String> {
    //根据用户和课程查询选课记录，用于判断是否添加选课
    XcLearningCourse findXcLearningCourseByUserIdAndCourseId(String userId, String courseId);
}