package org.jeecg.modules.pay.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IUserBusinessEntityService extends IService<UserBusinessEntity> {

    List<UserBusinessEntity> queryBusinessCodeByUserName(String userName, String channelCode);

    List<UserBusinessEntity> queryUserBusiness( String username);

    void deleteUserBusiness( UserBusinessEntity userBusinessEntity);

    Result<UserBusinessEntity> add(UserBusinessEntity userBusinessEntity);

    List<UserBusinessEntity> queryAllBusiness(UserBusinessEntity userBusinessEntity);

    void activeBusiness(String userName,String channelCode,String[] codes);
    void disableBusiness(String userName,String channelCode,String[] codes);
    void disableAllBusiness(String userName,String channelCode);

    void updateBusinessTodayAmount(OrderInfoEntity order);
    void updateBusinessTodayAmount();
}
