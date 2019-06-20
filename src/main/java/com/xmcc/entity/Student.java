package com.xmcc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Data  //get、set、toString 的组合
@Entity //表示为一个实体类 交给spring管理
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "student") //表名
@DynamicUpdate //动态修改 自动去除为null的字段
public class Student implements Serializable {

    //标识主键和自增策略  GenerationType.SEQUENCE oracle的
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer age;
    private String sex;
    private String address;

    public Student(String name, Integer age, String sex, String address) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.address = address;
    }
}
