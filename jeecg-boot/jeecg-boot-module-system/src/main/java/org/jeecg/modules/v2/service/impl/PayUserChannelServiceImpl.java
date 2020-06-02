package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.constant.UserTypeEnum;
import org.jeecg.modules.v2.dto.UserChannelParam;
import org.jeecg.modules.v2.entity.PayUserChannel;
import org.jeecg.modules.v2.mapper.PayUserChannelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> getChannelsByUserAndProduct(String userName,String productCode){
        QueryWrapper<PayUserChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("product_code",productCode).eq("del_flag",
            DeleteFlagEnum.NOT_DELETE.getValue());
        List<PayUserChannel> userChannels = getBaseMapper().selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(userChannels)){
            return userChannels.stream().map(userChannel -> userChannel.getChannelCode()).collect(Collectors.toList());
        }
        return null;
    }

    public String checkParam(UserChannelParam param){
        StringBuilder msg = new StringBuilder();
        if(StringUtils.isEmpty(param.getProductCode())){
            msg.append("产品类型不能为空");
        }
        if(StringUtils.isEmpty(param.getChannelCode())){
            msg.append("通道不能为空");
        }
        if(param.getMemberType().equals(UserTypeEnum.AGENT.getValue())){
            if(StringUtils.isEmpty(param.getBusinessCode())){
                msg.append("子账号不能为空");
            }
            if(StringUtils.isEmpty(param.getBusinessApiKey())){
                msg.append("秘钥不能为空");
            }
        }
        return msg.toString();
    }

    public boolean channelExist(String userName,String productCode,String channelCode){
        QueryWrapper<PayUserChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("product_code",productCode).eq("channel_code",channelCode).eq("del_flag",
                DeleteFlagEnum.NOT_DELETE.getValue());
        List<PayUserChannel> userChannels = getBaseMapper().selectList(queryWrapper);
        if(CollectionUtils.isEmpty(userChannels)){
            return false;
        }
        return true;
    }
}
