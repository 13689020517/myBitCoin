package com.bitcoin.bean;

import com.bitcoin.utils.RSAUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

//钱包
//作用就是生成并保存公钥私钥
public class wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String name;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public wallet(String name) {
        this.name = name;
        File privateKeyFile = new File(name + "-key.pri");
        File publicKeyFile = new File(name + "-key.pub");
        if (!privateKeyFile.exists() || privateKeyFile.length() == 0 || !publicKeyFile.exists() || publicKeyFile.length() == 0) {
            RSAUtils.generateKeysJS("RSA", name + "-key.pri", name + "-key.pub");
        }
    }

    //发起转账:
    //付款方公钥
    //收款方公钥
    //付款方私钥（签名）
    //金额
    public transaction startTransaction(String receiveAddr, String content) {
        String sendAddr = Base64.encode(publicKey.getEncoded());
        String sign = RSAUtils.getSignature("ShA256withRSA", privateKey, content);
        return new transaction(sendAddr, receiveAddr, content, sign);
    }

    public static void main(String[] args) {
        wallet a = new wallet("a");
        wallet b = new wallet("b");
    }

}
