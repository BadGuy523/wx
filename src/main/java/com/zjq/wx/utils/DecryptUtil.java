package com.zjq.wx.utils;

import com.zjq.wx.utils.wxutils.AesException;
import com.zjq.wx.utils.wxutils.WXBizMsgCrypt;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @description: 加解密
 * @author: BadGuy
 * @date: 2020-03-24 12:09
 **/
public class DecryptUtil {

    public static String getResponse(String nonce,String openid) throws AesException, ParserConfigurationException, IOException, SAXException {
        // 第三方回复公众平台
        System.out.println("传入的openid为"+openid);
        // 需要加密的明文
        String encodingAesKey = "ZRMuzmTGx08jW6ZldmLsATyFSBJjwwcG8L4SFI8XUst";
        String token = "badguy523token";
        String timestamp = "1409304348";
        String appId = "wx5cdec8164ec728f7";
        String replyMsg = "<xml>" +
                "<ToUserName><![CDATA["+openid+"]]></ToUserName>" +
                "<FromUserName><![CDATA[BadGuy523]]></FromUserName>" +
                "<CreateTime>12345678</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[你好]]></Content>" +
                "</xml>";

        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);
        System.out.println("加密后: " + mingwen);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(mingwen);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);

        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        NodeList nodelist2 = root.getElementsByTagName("MsgSignature");

        String encrypt = nodelist1.item(0).getTextContent();
        String msgSignature = nodelist2.item(0).getTextContent();

        String format = "<xml><ToUserName><![CDATA["+openid+"]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";
        String fromXML = String.format(format, encrypt);

        // 公众平台发送消息给第三方，第三方处理

        // 第三方收到公众号平台发送的消息
        String result2 = pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
        System.out.println("解密后明文: " + result2);
        return mingwen;
        //pc.verifyUrl(null, null, null, null);
    }

}
