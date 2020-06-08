package org.jeecg.modules.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.service.ICommonApiService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.v2.constant.UserTypeEnum;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.entity.PayChannel;
import org.jeecg.modules.v2.entity.PayUserChannel;
import org.jeecg.modules.v2.entity.PayUserProduct;
import org.jeecg.modules.v2.service.impl.PayBusinessServiceImpl;
import org.jeecg.modules.v2.service.impl.PayChannelServiceImpl;
import org.jeecg.modules.v2.service.impl.PayUserChannelServiceImpl;
import org.jeecg.modules.v2.service.impl.PayUserProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
@Slf4j
public class CommonApiServiceImpl implements ICommonApiService {
    @Autowired
    private PayUserChannelServiceImpl userChannelService;
    @Autowired
    private PayBusinessServiceImpl businessService;
    @Autowired
    private PayChannelServiceImpl channelService;
    @Autowired
    private PayUserProductServiceImpl userProductService;
    @Override
    public String getRate(PayUserChannel userChannel) {
        if (userChannel.getUserRate() == null) {
            // 如果用户未配置费率，则使用通道费率
            PayChannel channel = channelService.getChannelByChannelCode(userChannel.getChannelCode());
            return channel.getChannelRate();
        }
        return userChannel.getUserRate();
    }
    @Override
    public String getGateWayUrl(PayUserChannel userChannel){
        PayChannel channel = channelService.getChannelByChannelCode(userChannel.getChannelCode());
        return channel.getChannelGateway();
    }
    @Override
    public PayUserChannel findChannel(String userName, String productCode) {
        List<PayUserChannel> channels = userChannelService.getUserChannels(userName, productCode);
        if (CollectionUtils.isEmpty(channels)) {
            throw new BusinessException("无通道权限，产品代码为：" + productCode);
        }
        PayUserChannel channel = channels.get(0);
        userChannelService.updateChannelLastUsedTime(channel);
        return channel;
    }

    @Override
    public PayBusiness findBusiness(String agentName, String channelCode, String productCode) {
        List<PayBusiness> businesses = businessService.getBusiness(agentName, channelCode, productCode);
        if (CollectionUtils.isEmpty(businesses)) {
            throw new BusinessException("代理[" + agentName + "]无可用子账号信息");
        }
        PayBusiness business = businesses.get(0);
        businessService.updateUsedTime(business);
        return business;
    }

    @Override
    public void checkSubmitAmountLegal(BigDecimal submitAmount, PayUserChannel channel) throws Exception {
        if (channel.getLowerLimit() != null && submitAmount.compareTo(channel.getLowerLimit()) < 0) {
            throw new BusinessException("申请金额低于最低金额，最低金额为：" + channel.getLowerLimit());
        }
        if (channel.getUpperLimit() != null && submitAmount.compareTo(channel.getUpperLimit()) > 0) {
            throw new BusinessException("申请金额高于最高金额，最高金额为：" + channel.getUpperLimit());
        }
    }

    @Override
    public void checkProduct(String userName, String productCode) {
        PayUserProduct userProduct = userProductService.getUserProducts(userName, productCode);
        if (userProduct == null) {
            throw new BusinessException("未配置产品权限，产品代码为：" + productCode);
        }
    }

    @Override
    public void checkSalesmanRate(SysUser user,PayUserChannel userChannel) {
        if(StringUtils.isNotBlank(user.getSalesmanUsername())){
            PayUserChannel saleChannel = userChannelService.getUserChannel(user.getSalesmanUsername(),userChannel.getChannelCode(),userChannel.getProductCode());
            if(saleChannel == null || StringUtils.isBlank(saleChannel.getUserRate())){
                log.error("未对介绍人设置费率，介绍人：{}",user.getSalesmanUsername());
                throw new BusinessException("未设置费率,请先在后台进行费率设置") ;
            }
        }
    }
}
