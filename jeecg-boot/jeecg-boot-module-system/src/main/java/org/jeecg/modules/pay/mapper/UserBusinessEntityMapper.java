package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserBusinessEntityMapper extends BaseMapper<UserBusinessEntity> {

    String queryBusinessCodeByUserName(@Param("userName")String userName);
}
