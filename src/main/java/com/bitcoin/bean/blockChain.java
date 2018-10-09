package com.bitcoin.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class blockChain {

    private static volatile blockChain instance;
    ArrayList<String> list = new ArrayList<String>();

    //对象new出时就去从文件中读取持久保存的数据
    private blockChain() {
        File file = new File("record.json");
        if (file.exists() && file.length() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, String.class);
            try {
                list = objectMapper.readValue(file, javaType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 单例模式
    public static blockChain getInstance() {
        if (instance == null) {
            synchronized (blockChain.class) {
                instance = new blockChain();
            }
        }
        return instance;
    }

    //创世区块,即本链的第一个数据区块
    //必须第一个创建，且一条链只能有一个创世区块
    public void addFirstBlock(String firstBlock) {
        if (list.size() > 0) {
            throw new RuntimeException("已经有了创世区块，不能再添加！");
        }
        list.add(firstBlock);
    }

    //添加区块,即本链中一个个的交易区块
    //必须有了创世区块才能添加其他的区块
    public void addBlock(String recordItem) {
        if (list.size() == 0) {
            throw new RuntimeException("本链还没有创世区块，请先创建创世区块！");
        }
        list.add(recordItem);
        save2Disk();
    }

    //查看所有区块记录
    public ArrayList<String> showBlockChainData() {
        return list;
    }

    //持久化的保存数据
    public void save2Disk() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("record.json"), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
