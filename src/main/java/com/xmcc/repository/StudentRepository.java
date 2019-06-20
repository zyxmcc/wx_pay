package com.xmcc.repository;

import com.xmcc.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//第一个参数 是实体类名称  第二个参数是主键类型
public interface StudentRepository extends JpaRepository<Student,Integer> {
    //根据ids查询学生集合
    List<Student> findStudentsByIdIn(List<Integer> ids);
}
