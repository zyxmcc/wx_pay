package com.xmcc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
@RequestMapping("weixin")
public class WeiXinController {

    @RequestMapping("getCode")
    public void getCode(@RequestParam("code") String code){
        log.info("访问了getCode的回调方法");
        log.info("回调的code-获取授权码：{}",code);

        //获取到授权码之后，通过授权码去得到令牌
        String url ="https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxcec0b9e65c084712&secret=05a7e861c1985ced86af77fb8f7163bc&code="+code+"&grant_type=authorization_code";
        RestTemplate restTemplate = new RestTemplate();
        String forObject = restTemplate.getForObject(url, String.class);
        log.info("授权令牌:{}",forObject);
    }

}
