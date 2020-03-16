package org.jeecg.modules.productChannel.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.productChannel.entity.ProductChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 产品关联通道
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
public interface ProductChannelMapper extends BaseMapper<ProductChannel> {

    @Select("select channel_code from pay_product_channel where product_code=#{productCode}")
    List<String> getProductChannelByProductCode(@Param("productCode") String productCode);

    @Delete("delete from pay_product_channel where product_code=#{productCode}")
     void remove(@Param("productCode") String productCode);

    Integer batchSave(@Param("channels") List<ChannelEntity> channels, @Param("productCode") String productCode);

    @Select("select channel_code,channel_name,product_code from pay_product_channel where product_code=#{productCode}")
    List<ProductChannel> getChannelProduct(@Param("productCode")String productCode);
}
