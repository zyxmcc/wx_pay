package com.xmcc;

import com.google.common.collect.Lists;
import com.xmcc.entity.Student;
import com.xmcc.repository.StudentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WxPayApplicationTests {
	@Autowired
	private StudentRepository studentRepository;
	@Test
	public void contextLoads() {
		//List<Student> all = studentRepository.findAll();
		//all.stream().forEach(System.out::println);
		//studentRepository.save(new Student("迪丽热巴",25,"女","新疆"));
		List<Student> all = studentRepository.findStudentsByIdIn(Lists.newArrayList(2,4,6));
		all.stream().forEach(System.out::println);
	}

}
