package org.jeecg.modules.wallet.controller;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet")
@Slf4j
public class WalletController {

    @PostMapping("/create")
    public R<String> create(String json){
        return null;
    }
}
