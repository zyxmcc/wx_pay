package com.xmcc.config;

import com.lly835.bestpay.config.WxPayH5Config;
import com.lly835.bestpay.service.BestPayService;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class payConfig {
    @Autowired
    private WeiXinProperties weixinProperties;

    @Bean
    public BestPayService bestPayService(){
        //微信公众账号支付配置
        WxPayH5Config wxPayH5Config = new WxPayH5Config();
        wxPayH5Config.setAppId(weixinProperties.getAppid());
        wxPayH5Config.setAppSecret(weixinProperties.getSecret());
        wxPayH5Config.setMchId(weixinProperties.getMchId());
        wxPayH5Config.setMchKey(weixinProperties.getMchKey());
        wxPayH5Config.setKeyPath(weixinProperties.getKeyPath());
        wxPayH5Config.setNotifyUrl(weixinProperties.getNotifyUrl());

        //支付类, 所有方法都在这个类里
        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayH5Config(wxPayH5Config);

        return bestPayService;
    }
}
