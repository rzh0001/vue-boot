package org.jeecg.modules.v2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.v2.entity.PayProductChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 产品通道
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
public interface PayProductChannelMapper extends BaseMapper<PayProductChannel> {

    @Delete("update pay_v2_product_channel set del_flag=1 where product_code=#{productCode} and channel_code=#{channelCode}")
    void delete(@Param("productCode") String productCode,@Param("channelCode") String channelCode);
    @Delete("delete from pay_v2_product_channel where product_code=#{productCode}")
    void cleanRelated(@Param("productCode") String productCode);

    void saveProductChannelCodes(@Param("productCode") String productCode, @Param("channelCodes") List<String> channelCodes);

    @Select("select channel_code from pay_v2_product_channel where product_code=#{productCode} and del_flag=0 and status=1")
    List<String> getProductRelateChannels(@Param("productCode") String productCode);
}
