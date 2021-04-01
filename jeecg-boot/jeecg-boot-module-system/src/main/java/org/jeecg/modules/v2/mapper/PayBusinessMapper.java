package org.jeecg.modules.v2.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.v2.dto.ChargeBusinessParam;
import org.jeecg.modules.v2.entity.PayBusiness;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 子账号信息
 * @Author: jeecg-boot
 * @Date: 2020-06-01
 * @Version: V1.0
 */
public interface PayBusinessMapper extends BaseMapper<PayBusiness> {
    @Update("update pay_v2_business set business_recharge_amount=#{param.chargeAmount}+business_recharge_amount where user_name=#{param.userName} and channel_code=#{param.channelCode} and product_code=#{param.productCode} and business_code=#{param.businessCode} and del_flag=0 and business_active_status='1'")
    void updateBusinessAmount(@Param("param") ChargeBusinessParam param);

    @Update("update pay_v2_business set business_recharge_amount=business_recharge_amount-#{amount} where user_name=#{userName} and channel_code=#{channelCode} and product_code=#{productCode} and business_code=#{businessCode} and del_flag=0 and business_active_status='1'")
    void subtractAmount(@Param("amount") BigDecimal amount, @Param("userName") String userName,
        @Param("channelCode") String channelCode, @Param("productCode") String productCode,
        @Param("businessCode") String businessCode);
}
