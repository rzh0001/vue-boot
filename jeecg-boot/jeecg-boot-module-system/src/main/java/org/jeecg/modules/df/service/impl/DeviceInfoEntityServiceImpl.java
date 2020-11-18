package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import org.jeecg.modules.df.entity.DeviceInfoEntity;
import org.jeecg.modules.df.mapper.DeviceInfoEntityMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date: 2020-11-16
 * @Version: V1.0
 */
@Service
public class DeviceInfoEntityServiceImpl extends ServiceImpl<DeviceInfoEntityMapper, DeviceInfoEntity> {

    public DeviceInfoEntity findByCode(String deviceCode) {
        return getBaseMapper().selectOne(new LambdaQueryWrapper<DeviceInfoEntity>().
                eq(DeviceInfoEntity::getDeviceCode, deviceCode));
    }
}
