package com.bitcoin.bean;

import com.bitcoin.utils.HashUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//区块链账本
public class blockChain {

    private static volatile blockChain instance;
    ArrayList<block> list = new ArrayList<block>();

    //对象new出时就去从文件中读取持久保存的数据
    private blockChain() {
        File file = new File("record.json");
        if (file.exists() && file.length() > 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, block.class);
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
        String preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        int proof = mine(firstBlock, preHash);
        list.add(new block(list.size() + 1, firstBlock, HashUtils.sha256(firstBlock + proof + preHash), proof, preHash));
        save2Disk();
    }

    //添加区块,即本链中一个个的交易区块
    //必须有了创世区块才能添加其他的区块
    public void addBlock(transaction recordItem) {
        if (list.size() == 0) {
            throw new RuntimeException("本链还没有创世区块，请先创建创世区块！");
        }
        String preHash = list.get(list.size() - 1).hash;
        int proof = mine(recordItem.getContent(), preHash);
        list.add(new block(list.size() + 1, recordItem.getContent(), HashUtils.sha256(recordItem.getContent() + proof + preHash), proof, preHash));
        save2Disk();
    }

    //查看所有区块记录
    public ArrayList<block> showBlockChainData() {
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

    //校验数据是否被篡改
    public String verify() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (block block : list) {
            String content = block.content;
            int proof = block.proof;
            String preHash = block.preHash;
            String calculateHash = HashUtils.sha256(content + proof + preHash);
            String hash = block.hash;
            if (!calculateHash.equals(hash)) {
                sb.append("ID为" + block.id + "的区块数据被篡改！<br/>");
            }
            if (i > 0) {
                block preBlock = list.get(i - 1);
                String preBlockHash = preBlock.hash;
                if (!preBlockHash.equals(block.preHash)) {
                    sb.append("ID为" + block.id + "的区块数据中的preHash有问题，可能有数据被篡改！<br/>");
                }
            }
        }
        return sb.toString();
    }

    //挖矿算法简易版本，返回值为工作量证明，比特币使用的这套共识算法叫POW，proof of Work
    private int mine(String content, String preHash) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hash = HashUtils.sha256(content + i + preHash);
            if (hash.startsWith("0000")) {
                System.out.println("挖矿成功");
                return i;
            } else {
                System.out.println("第" + i + "次尝试挖矿");
            }
        }
        throw new RuntimeException("挖矿失败");
    }

    // 要去做数据的比对,然后判断是否接受对方传递过来的区块链数据
    // 1.比对长度，长的那条链就是最新版本
    // 2.数据校验
    public void checkNewList(List<block> newList) {
        if (newList.size() > list.size()) {
            list = (ArrayList<block>) newList;
        }
    }

}
