package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Lists;
import org.jeecg.modules.df.entity.DeviceUserEntity;
import org.jeecg.modules.df.mapper.DeviceUserEntityMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 商户关联设备
 * @Author: jeecg-boot
 * @Date:   2020-11-18
 * @Version: V1.0
 */
@Service
public class DeviceUserEntityServiceImpl extends ServiceImpl<DeviceUserEntityMapper, DeviceUserEntity> implements IService<DeviceUserEntity> {


    public List<DeviceUserEntity> findByCode(String code){
        return getBaseMapper().selectList(new LambdaQueryWrapper<DeviceUserEntity>().eq(DeviceUserEntity::getDeviceCode,code));
    }

    public void batchSave(String deviceCode,List<String> userNames){
        List<DeviceUserEntity> batch = Lists.newArrayList();
        for(String userName:userNames){
            DeviceUserEntity deviceUser = DeviceUserEntity.builder().deviceCode(deviceCode).userName(userName).build();
            batch.add(deviceUser);
        }
        saveBatch(batch);
    }
}
