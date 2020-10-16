package org.jeecg.modules.wallet.controller;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.wallet.dto.WalletHttpCallbackParam;
import org.jeecg.modules.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletController {
    @Autowired
    private WalletService walletService;
    @PostMapping("/create")
    public R<String> create(String json){
        return null;
    }

    @PostMapping(value ="/callback", produces="application/x-www-form-urlencoded;charset=UTF-8" )
    public void callback( WalletHttpCallbackParam param) throws Exception {
        walletService.callback(param);
    }
}
