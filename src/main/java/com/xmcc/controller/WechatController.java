package com.xmcc.controller;

import com.xmcc.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Controller
@RequestMapping("wechat")
@Slf4j
public class WechatController {

    @Autowired//注入配置的对象
    private WxMpService wxMpService;

    //根据API接口文档 书写路径
    @RequestMapping("authorize")
    //查看文档需要一个returnUrl参数
    public String authorize(@RequestParam("returnUrl") String returnUrl) throws UnsupportedEncodingException {
        //自己编写获得openid的路径 在下面定义方法getUserInfo
        String url = "http://xmcc-zy.natapp1.cc/sell/wechat/getUserInfo";
        //根据sdk文档获得路径  点击方法下载文档 很清晰的解释
        /**
         * 第一个参数是获得授权码code后回调的地址
         * 第二个是策略：获得简单的授权，还是希望获得用户的信息
         * 第三个参数是我们希望携带的参数:查看API文档需要返回returnUrl 所以我们就携带它
         */
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url,
                WxConsts.OAuth2Scope.SNSAPI_USERINFO,
                URLEncoder.encode(returnUrl,"UTF-8"));
        log.info("跳转的地址:{}",redirectUrl);

        return "redirect:"+redirectUrl;

    }


    /**
     * code:是授权码
     * returnUrl：是刚才我们自己传递的参数  会传递到微信然后传回来
     */
    @RequestMapping("getUserInfo")
    public String getUserInfo(@RequestParam("code")String code,
                              @RequestParam("state") String returnUrl) throws UnsupportedEncodingException {
        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = null;
        try {
            //根据sdk文档 通过code获得令牌与openid
            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);

        } catch (WxErrorException e) {
            log.error("微信获得access_token异常:{}",e.getError().getErrorMsg());
            throw  new CustomException(e.getError().getErrorCode(),e.getError().getErrorMsg());
        }

        try {
            //获得用户信息  ,授权其实用不到的 这儿打出来看看
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            log.info("获得用户信息:{}",wxMpUser.getNickname());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        //获得openid
        String openId = wxMpOAuth2AccessToken.getOpenId();
        log.info("openid为:{}",openId);
        log.info("重定向地址为:{}",URLDecoder.decode(returnUrl,"UTF-8")+"?openid="+openId);
        return  "redirect:"+ URLDecoder.decode(returnUrl,"UTF-8")+"?openid="+openId;
    }
    //测试是否获得openid
    @RequestMapping("testOpenid")
    public void testOpenid(@RequestParam("openid")String openid){
        log.info("获得用户的openid为:{}",openid);
    }
}
