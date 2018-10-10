package com.bitcoin.bean;

import com.bitcoin.utils.RSAUtils;

import java.security.PublicKey;

//交易实体类
//一笔交易应该包含：付款方公钥，收款方公钥，金额/附加信息,签名（私钥）
public class transaction {
    private String sendAddr;
    private String receiveAddr;
    private String content;
    private String sign;

    public transaction() {
    }

    public String getSendAddr() {
        return sendAddr;
    }

    public void setSendAddr(String sendAddr) {
        this.sendAddr = sendAddr;
    }

    public String getReceiveAddr() {
        return receiveAddr;
    }

    public void setReceiveAddr(String receiveAddr) {
        this.receiveAddr = receiveAddr;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public transaction(String sendAddr, String receiveAddr, String content, String sign) {
        this.sendAddr = sendAddr;
        this.receiveAddr = receiveAddr;
        this.content = content;
        this.sign = sign;
    }

    //验证交易是否合法
    public boolean verify(){
        PublicKey publicKey = RSAUtils.getPublicKeyFromString("RSA", sendAddr);
        return RSAUtils.verifyDataJS("SHA256withRSA", publicKey, content, sign);
    }

}
