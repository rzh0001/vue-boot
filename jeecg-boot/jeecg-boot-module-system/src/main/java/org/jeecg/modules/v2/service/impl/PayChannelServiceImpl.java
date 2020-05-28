package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.entity.PayChannel;
import org.jeecg.modules.v2.mapper.PayChannelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: channel
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class PayChannelServiceImpl extends ServiceImpl<PayChannelMapper, PayChannel> implements IService<PayChannel> {
    /**
     * 获取所有通道信息
     * @return
     */
    public List<PayChannel> getAllChannel(){
        QueryWrapper<PayChannel> queryWrapper = new QueryWrapper<>();
        return getBaseMapper().selectList(queryWrapper);
    }

    public PayChannel getChannelByChannelCode(String channelCode){
        return getBaseMapper().selectOne(new LambdaQueryWrapper<PayChannel>().eq(PayChannel::getChannelCode,channelCode));
    }

    public void deleteById(String id){
        getBaseMapper().deleteById(id);
    }
}
