package com.xmcc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController 整合了Controller和ResponseBody
 */
@RestController
@RequestMapping("/hello")
@Slf4j
public class MyHelloController1 {

    @RequestMapping("/hello")
    @GetMapping
    public String hello(){
        log.info("这是测试的日志");
        return "springBoot hello";
    }
}
