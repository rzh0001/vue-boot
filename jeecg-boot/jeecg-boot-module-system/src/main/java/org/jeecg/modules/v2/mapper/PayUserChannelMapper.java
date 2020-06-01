package org.jeecg.modules.v2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
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

    @Update("update pay_v2_user_channel set last_used_time=now() where user_name=#{channel.userName} and channel_code=#{channel.channelCode} and product_code=#{channel.productCode} and del_flag=0")
    void updateChannelLastUsedTime(@Param("channel") PayUserChannel channel);
}
