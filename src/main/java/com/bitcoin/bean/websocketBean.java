package com.bitcoin.bean;

public class websocketBean {
    // 标识消息的作用
       // 1 传递区块链数据
       // 2 节点数据
       // 3 区块
       // 4 交易数据
    public int code;

    public String msg;

    public websocketBean() {
    }

    public websocketBean(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
