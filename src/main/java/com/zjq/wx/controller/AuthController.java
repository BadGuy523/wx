package com.zjq.wx.controller;

import com.zjq.wx.utils.DecryptUtil;
import com.zjq.wx.utils.WeixinCheckoutUtil;
import com.zjq.wx.utils.wxutils.AesException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 微信开发服务器配置
 * @author: BadGuy5
 * @date: 2020-03-23 22:00
 **/
@Slf4j
@RestController
public class AuthController {

    private final String token = "badguy523token";

    @GetMapping("/auth_token")
    public String getEchoStr(String signature,String timestamp,String nonce,String echostr) throws NoSuchAlgorithmException {
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (signature != null && WeixinCheckoutUtil.checkSignature(signature, timestamp, nonce)) {
            return echostr;
        }

        return null;
    }

    @PostMapping("/auth_token")
    public String getMsgAndSend(HttpServletRequest request) throws ParserConfigurationException, AesException, SAXException, IOException, DocumentException, DocumentException {

        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        InputStream ins = request.getInputStream();
        Document doc = (Document) reader.read(ins);
        Element root = doc.getRootElement();
        List<Element> list = root.elements();
        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        ins.close();
        String toUserName = map.get("ToUserName");
        String fromUserName = map.get("FromUserName");
        log.info("ToUserName:"+toUserName);
        log.info("FromUserName:"+fromUserName);
        Enumeration<String> enumeration = request.getParameterNames();
        while(enumeration.hasMoreElements()) {
            log.info("请求参数名："+enumeration.nextElement());
        }
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((key, value) -> {
            log.info("请求参数map-key："+key);
            for (String string : value) {
                log.info("请求参数map-val："+string);
            }
        });
        String openId = request.getParameter("openid");
        log.info("--openid:"+request.getParameter("openid"));
        log.info("--openid:"+request.getParameter("openid"));
        log.info("--ToUserName:"+request.getParameter("ToUserName"));
        log.info("--FromUserName:"+request.getParameter("FromUserName"));
        log.info("--openId:"+openId);
        //return DecryptUtil.getResponse(request.getParameter("nonce"),request.getParameter("openid"));
        return "<xml><ToUserName><![CDATA["+fromUserName+"]]></ToUserName><FromUserName><![CDATA["+toUserName+"]]></FromUserName><CreateTime>12345678</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[你好]]></Content></xml>";
    }

}
