package org.jeecg.modules.pay.service.impl;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.mapper.UserBusinessEntityMapper;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class UserBusinessEntityServiceImpl extends ServiceImpl<UserBusinessEntityMapper, UserBusinessEntity> implements IUserBusinessEntityService {
    @Autowired
    private ISysUserService userService;

    @Override
    public List<UserBusinessEntity> queryBusinessCodeByUserName(String userName, String channelCode) {
        return baseMapper.queryBusinessCodeByUserName(userName,channelCode);
    }

    @Override
    public List<UserBusinessEntity> queryUserBusiness(String username) {
        return baseMapper.queryUserBusiness(username);
    }

    @Override
    public void deleteUserBusiness(UserBusinessEntity userBusinessEntity) {
        baseMapper.deleteUserBusiness(userBusinessEntity);
    }

    @Override
    public Result<UserBusinessEntity> add(UserBusinessEntity userBusinessEntity) {
        Result<UserBusinessEntity> result = new Result<UserBusinessEntity>();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser user = userService.getUserByName(loginUser.getUsername());
        String userName = userBusinessEntity.getUserName();
        SysUser addUser = userService.getUserByName(userName);
        if (addUser == null) {
            result.error500("商户不存在");
            return result;
        }
        //当前登录的用户如果是代理，则只能添加自己
        if (BaseConstant.USER_AGENT.equals(user.getMemberType()) && !user.getUsername().equals(userName)) {
            result.error500("没有权限为代理名称为："+userName+"添加挂马信息，请联系管理员添加");
            return result;
        }
        //校验在该通道下的该代理是否已经关联过挂码账号
        List<UserBusinessEntity> list = queryBusinessCodeByUserName(userName,
                userBusinessEntity.getChannelCode());
        if (!CollectionUtils.isEmpty(list) && list.get(0).getBusinessCode().equals(userBusinessEntity.getBusinessCode())) {
            result.error500("通道：" + userBusinessEntity.getChannelCode() + "下，该代理：" + userName + "已经关联过挂马账号：" + userBusinessEntity.getBusinessCode());
            return result;
        }
        this.save(userBusinessEntity);
        result.success("添加成功！");
        return result;
    }
}
