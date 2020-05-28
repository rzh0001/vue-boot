package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.entity.PayUserProduct;
import org.jeecg.modules.v2.mapper.PayUserProductMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户关联产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Service
public class PayUserProductServiceImpl extends ServiceImpl<PayUserProductMapper, PayUserProduct> implements
    IService<PayUserProduct> {

}
