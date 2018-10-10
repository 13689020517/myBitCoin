package com.bitcoin.bean;

public class block {
    //ID
    public int id;
    //内容
    public String content;
    //本区块的哈希值
    public String hash;
    //工作量证明
    public int proof;
    //上一个区块的哈希值
    public String preHash;

    public block() {
    }

    public block(int id, String content, String hash, int proof, String preHash) {
        this.id = id;
        this.content = content;
        this.hash = hash;
        this.proof = proof;
        this.preHash = preHash;
    }
}
