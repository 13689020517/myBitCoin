package com.bitcoin.controller;

import com.bitcoin.bean.block;
import com.bitcoin.bean.blockChain;
import com.bitcoin.bean.transaction;
import com.bitcoin.bean.websocketBean;
import com.bitcoin.mainApp;
import com.bitcoin.net.MyClient;
import com.bitcoin.net.MyServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
public class operateChainController {

    @PostMapping("addFirstBlock")
    @ResponseBody
    public String addFirstBlock(String start) {
        try {
            blockChain.getInstance().addFirstBlock(start);
            return "成功创建创世区块！";
        } catch (Exception e) {
            return "创建失败，失败原因" + e.getMessage();
        }
    }

    @PostMapping("addBlock")
    @ResponseBody
    public String addBlock(transaction tran) {
        try {
            //电子校验，检查交易数据在传输过程中是否被篡改
            if (tran.verify()) {
                blockChain.getInstance().addBlock(tran);

                //挖矿成功了就包装数据立刻广播出去，好让其他节点更新账本
                websocketBean bean = new websocketBean(4, tran.getContent());
                ObjectMapper objectMapper = new ObjectMapper();
                String msg = objectMapper.writeValueAsString(bean);
                server.broadcast(msg);

                return "添加交易成功！";
            } else {
                return "数据校验失败！您的这笔交易可能被篡改！";
            }
        } catch (Exception e) {
            return "创建失败，失败原因" + e.getMessage();
        }
    }

    @GetMapping("showBlockChainData")
    @ResponseBody
    public List<block> showBlockChainData() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return blockChain.getInstance().showBlockChainData();
    }

    @GetMapping("verify")
    @ResponseBody
    public String verify() {
        String verify = blockChain.getInstance().verify();
        if (StringUtils.isEmpty(verify)) {
            return "未检测到区块数据被篡改";
        }
        return verify;
    }

    private HashSet<String> set = new HashSet<>();

    // 注册节点
    @PostMapping("regist")
    public String regist(String port) {
        set.add(port);
        return "注册成功";
    }

    // 存放所有连接websocket服务端的客户端
    private List<MyClient> clients = new ArrayList<>();

    // 连接
    @PostMapping("conn")
    public String conn() throws Exception {
        // 遍历本地存储的服务端地址,然后连接
        for (String node : set) {
            URI uri = new URI("ws://localhost:" + node);
            MyClient client = new MyClient(uri, node);
            client.connect();
            clients.add(client);
        }
        return "连接成功";
    }

    private MyServer server;

    // Controller创建后立刻调用该方法
    @PostConstruct
    public void init() {
        // 将springboot启动的端口号+1,作为WebSocket服务端启动时使用的端口号
        server = new MyServer(Integer.parseInt(mainApp.port) + 1);
        server.start();
    }

    // 请求同步数据,
    // 模拟场景 : 全新的一个节点上线了,但是没有区块链的数据,发送一个请求,要求获取其他节点上存储的区块链数据
    @PostMapping("syncData")
    public String syncData() {
        for (MyClient client : clients) {
            client.send("亲,把你的区块链数据给我一份");
        }
        return "连接成功";
    }

}
