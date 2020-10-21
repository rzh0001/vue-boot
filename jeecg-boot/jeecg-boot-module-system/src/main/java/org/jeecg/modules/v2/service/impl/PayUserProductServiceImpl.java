package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.constant.StatusEnum;
import org.jeecg.modules.v2.constant.UserTypeEnum;
import org.jeecg.modules.v2.entity.PayUserProduct;
import org.jeecg.modules.v2.mapper.PayUserProductMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户关联产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class PayUserProductServiceImpl extends ServiceImpl<PayUserProductMapper, PayUserProduct> implements
    IService<PayUserProduct> {
    /**
     * 获取商户关联的产品
     * @param userName
     * @param productCode
     * @return
     */
    public PayUserProduct getUserProducts(String userName,String productCode){
        QueryWrapper<PayUserProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("product_code",productCode).eq("member_type", UserTypeEnum.MERCHANT.getValue()).eq("status",
            StatusEnum.OPEN.getValue()).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        return getBaseMapper().selectOne(queryWrapper);
    }


    public List<String> findProductCodesByUserName(String userName){
        QueryWrapper<PayUserProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("status", StatusEnum.OPEN.getValue()).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        List<PayUserProduct> userProducts = getBaseMapper().selectList(queryWrapper);
        if(!CollectionUtils.isEmpty(userProducts)){
            return userProducts.stream().map(userProduct->userProduct.getProductCode()).collect(Collectors.toList());
        }
        return null;
    }

    public boolean existProductByUserName(String userName,String productCode){
        QueryWrapper<PayUserProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",userName).eq("product_code",productCode).eq("status",
            StatusEnum.OPEN.getValue()).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        List<PayUserProduct> products = getBaseMapper().selectList(queryWrapper);
        if(CollectionUtils.isEmpty(products)){
            return false;
        }
        return true;
    }

}