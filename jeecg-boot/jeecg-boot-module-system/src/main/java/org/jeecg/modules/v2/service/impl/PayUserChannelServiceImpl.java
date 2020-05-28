package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.entity.PayUserChannel;
import org.jeecg.modules.v2.mapper.PayUserChannelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class PayUserChannelServiceImpl extends ServiceImpl<PayUserChannelMapper, PayUserChannel> implements
    IService<PayUserChannel> {

    public void delete(String productCode,String channelCode){

    }
}
