package com.bitcoin.controller;

import com.bitcoin.bean.blockChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public String addBlock(String content) {
        try {
            blockChain.getInstance().addBlock(content);
            return "添加交易成功！";
        } catch (Exception e) {
            return "创建失败，失败原因" + e.getMessage();
        }
    }

    @GetMapping("showBlockChainData")
    public List<String> showBlockChainData() {
       return blockChain.getInstance().showBlockChainData();
    }

    @GetMapping("verify")
    public void verify() {

    }

}
