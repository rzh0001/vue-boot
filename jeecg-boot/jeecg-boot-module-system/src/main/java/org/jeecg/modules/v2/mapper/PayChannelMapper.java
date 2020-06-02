package org.jeecg.modules.v2.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.v2.entity.PayChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: channel
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
public interface PayChannelMapper extends BaseMapper<PayChannel> {
    @Update("update pay_v2_channel set del_flag=1 where id=#{id}")
    void deleteById(@Param("id") String id);

    List<PayChannel> getChannlesByChannelCodes(@Param("channelCodes") List<String> channelCodes);
}
