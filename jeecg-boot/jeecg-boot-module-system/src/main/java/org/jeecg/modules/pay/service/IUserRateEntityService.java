package org.jeecg.modules.pay.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserRateEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IUserRateEntityService extends IService<UserRateEntity> {
    String getUserRateByUserNameAndAngetCode(String userName,String agentUsername,String payType);

    /**
     * 获取介绍人的费率
     * @param userName 介绍人名称
     * @param agentUsername 介绍人所属的高级代理
     * @param beIntroducerName 被介绍人
     * @return
     */
    String getBeIntroducerRate(String userName,String agentUsername,String beIntroducerName, String payType);

    List<UserRateEntity> queryUserRate(String username);

    void deleteUserRate(UserRateEntity dto);
    List<String> getBeIntroducerName(String userName);
}
