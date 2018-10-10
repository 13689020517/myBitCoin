package com.bitcoin.net;

import com.bitcoin.bean.websocketBean;
import com.bitcoin.bean.block;
import com.bitcoin.bean.blockChain;
import com.bitcoin.bean.transaction;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class MyClient extends WebSocketClient {

    private String name;

    public MyClient(URI serverUri, String name) {
        super(serverUri);
        this.name = name;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("客户端___" + name + "___打开了一个连接");
    }

    @Override
    public void onMessage(String message) {

        System.out.println("客户端___" + name + "___收到了消息:" + message);
        blockChain blockchain = blockChain.getInstance();
        try {
            if (!StringUtils.isEmpty(message)) {
                // 把收到的消息还原成对象
                ObjectMapper objectMapper = new ObjectMapper();
                websocketBean messageBean = objectMapper.readValue(message, websocketBean.class);

                if (messageBean.code == 1) {
                    // 表示收到的消息是区块链数据.List<block>
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, block.class);
                    List<block> newList = objectMapper.readValue(messageBean.msg, javaType);

                    // 要去做数据的比对,然后判断是否接受对方传递过来的区块链数据
                    blockchain.checkNewList(newList);
                } else if (messageBean.code == 4) {
                    // 交易数据
                    String transactionMsg = messageBean.msg;
                    // 添加数据到区块
                    blockchain.addBlock(new transaction(null,null,transactionMsg,null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端___" + name + "___关闭了一个连接");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端___" + name + "___发生了错误:" + ex.getMessage());
    }
}
