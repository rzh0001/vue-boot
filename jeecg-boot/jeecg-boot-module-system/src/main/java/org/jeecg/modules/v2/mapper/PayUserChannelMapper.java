package org.jeecg.modules.v2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.v2.entity.PayUserChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
public interface PayUserChannelMapper extends BaseMapper<PayUserChannel> {
    @Delete("update pay_v2_user_channel set del_flag=1 where channel_code=#{channelCode} and product_code=#{productCode}")
    void delete(@Param("productCode") String productCode,@Param("channelCode") String channelCode);
}
