package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.constant.BusinessActivStatusEnum;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.dto.ChargeBusinessParam;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.entity.PayChannel;
import org.jeecg.modules.v2.entity.PayProduct;
import org.jeecg.modules.v2.mapper.PayBusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 子账号信息
 * @Author: jeecg-boot
 * @Date: 2020-06-01
 * @Version: V1.0
 */
@Service
public class PayBusinessServiceImpl extends ServiceImpl<PayBusinessMapper, PayBusiness>
    implements IService<PayBusiness> {
    @Autowired
    private PayProductServiceImpl productService;
    @Autowired
    private PayChannelServiceImpl channelService;

    public List<PayBusiness> getBusiness(String userName, String channelCode, String productCode) {
        QueryWrapper<PayBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("channel_code", channelCode).eq("product_code", productCode)
            .eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue())
            .eq("business_active_status", BusinessActivStatusEnum.ACTIVE.getValue()).orderByAsc("last_used_time");
        return getBaseMapper().selectList(queryWrapper);
    }

    public List<PayBusiness> getBusinessNotDelete(String userName, String channelCode, String productCode) {
        QueryWrapper<PayBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("channel_code", channelCode).eq("product_code", productCode)
            .eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue()).orderByDesc("last_used_time");
        return getBaseMapper().selectList(queryWrapper);
    }

    public void updateUsedTime(PayBusiness business) {
        business.setLastUsedTime(new Date());
        getBaseMapper().updateById(business);
    }

    public boolean existBusiness(String userName, String productCode, String channelCode, String businessCode) {
        QueryWrapper<PayBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("channel_code", channelCode).eq("product_code", productCode)
            .eq("business_code", businessCode).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue())
            .eq("business_active_status", BusinessActivStatusEnum.ACTIVE.getValue());
        List<PayBusiness> businesses = getBaseMapper().selectList(queryWrapper);
        if (CollectionUtils.isEmpty(businesses)) {
            return false;
        }
        return true;
    }

    public List<PayBusiness> getBusinessByName(String userName) {
        QueryWrapper<PayBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        // .eq("business_active_status", BusinessActivStatusEnum.ACTIVE.getValue());
        return getBaseMapper().selectList(queryWrapper);
    }

    public void deleteBusiness(PayBusiness business) {
        business.setDelFlag(DeleteFlagEnum.DELETE.getValue());
        getBaseMapper().updateById(business);
    }

    public List<PayProduct> getBusinessRelatedProduct(String userName) {
        List<PayBusiness> businesses = this.getBusinessByName(userName);
        if (!CollectionUtils.isEmpty(businesses)) {
            List<String> productCodes =
                businesses.stream().map(business -> business.getProductCode()).collect(Collectors.toList());
            return productService.getProductsByProductCodes(productCodes);
        }
        return null;
    }

    public List<PayChannel> getBusinessRelatedChannel(String userName, String productCode) {
        QueryWrapper<PayBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName).eq("product_code", productCode)
            .eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue())
            .eq("business_active_status", BusinessActivStatusEnum.ACTIVE.getValue());
        List<PayBusiness> businesses = getBaseMapper().selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(businesses)) {
            List<String> channels =
                businesses.stream().map(business -> business.getChannelCode()).collect(Collectors.toList());
            return channelService.getChannlesByChannelCodes(channels);
        }
        return null;
    }

    public void updateBusinessAmount(ChargeBusinessParam param) {
        getBaseMapper().updateBusinessAmount(param);
    }

    public void subtractAmount(BigDecimal amount, String userName, String channelCode, String productCode,
        String businessCode) {
        getBaseMapper().subtractAmount(amount, userName, channelCode, productCode, businessCode);
    }
}
