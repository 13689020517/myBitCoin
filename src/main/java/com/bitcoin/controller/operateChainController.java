package com.bitcoin.controller;

import com.bitcoin.bean.block;
import com.bitcoin.bean.blockChain;
import com.bitcoin.bean.transaction;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
    public String addBlock(transaction tran) {
        try {
            //电子校验，检查交易数据在传输过程中是否被篡改
            if(tran.verify()){
                blockChain.getInstance().addBlock(tran);
                return "添加交易成功！";
            }else {
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
        if(StringUtils.isEmpty(verify)){
           return "未检测到区块数据被篡改";
        }
        return verify;
    }

}
