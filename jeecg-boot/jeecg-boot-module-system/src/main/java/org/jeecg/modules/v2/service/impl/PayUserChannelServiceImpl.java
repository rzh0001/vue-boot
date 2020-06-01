package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.constant.UserTypeEnum;
import org.jeecg.modules.v2.entity.PayUserChannel;
import org.jeecg.modules.v2.mapper.PayUserChannelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

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
        getBaseMapper().delete(productCode,channelCode);
    }

    public List<PayUserChannel> getUserChannels(String userName,String productCode){
        QueryWrapper<PayUserChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("product_code",productCode).eq("member_type", UserTypeEnum.MERCHANT.getValue()).eq("del_flag",
            DeleteFlagEnum.NOT_DELETE.getValue()).orderByDesc("last_used_time");
        return getBaseMapper().selectList(queryWrapper);
    }

    public void updateChannelLastUsedTime(PayUserChannel channel){
        getBaseMapper().updateChannelLastUsedTime(channel);
    }
}
