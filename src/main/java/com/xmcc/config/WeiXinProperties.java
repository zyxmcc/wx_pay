package com.xmcc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix="wechat")
public class WeiXinProperties {

    private String appid;
    private String secret;

    /**
     * 商户号
     */
    private String mchId;
    //商户秘钥
    private String mchKey;
    //商户的证书路径
    private String keyPath;
    //微信支付的异步通知
    private String notifyUrl;
}
