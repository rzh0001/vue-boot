package org.jeecg.modules.df.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.df.constant.DfConstant;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.mapper.PayOrderMapper;
import org.jeecg.modules.df.service.IPayOrderService;
import org.jeecg.modules.df.util.IDUtil;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2019-10-27
 * @Version: V1.0
 */
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private IUserAmountEntityService userAmountService;
    
    @Override
    public boolean create(PayOrder order) {
        LoginUser ou = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        
        // 检查代付额度
        BigDecimal userAmount = userAmountService.getUserAmount(ou.getId());
        if (userAmount.compareTo(order.getAmount()) < 0) {
            throw new RRException("余额不足，请及时充值！");
        }
        
        // 获取费率配置
        SysUser user = userService.getById(ou.getId());
        if (BeanUtil.isEmpty(user) || null == user.getTransactionFeeRate() || null == user.getOrderFixedFee()) {
            throw new RRException("获取费率配置失败！");
        }
        
        // 扣减余额
        userAmountService.changeAmount(order.getUserId(), order.getAmount().negate(), order.getOrderId(), "", "1");
    
        order.setUserId(user.getId());
        order.setUserName(user.getUsername());
        order.setUserRealname(user.getRealname());
        order.setAgentId(user.getAgentId());
        order.setAgentUsername(user.getAgentUsername());
        order.setAgentRealname(user.getAgentRealname());
        
        // 生成订单号
        order.setOrderId(IDUtil.genPayOrderId());
        order.setStatus(DfConstant.STATUS_SAVE);
        
        // 计算手续费
        order.setTransactionFee(order.getAmount().multiply(user.getTransactionFeeRate()));
        order.setFixedFee(user.getOrderFixedFee());
        order.setOrderFee(order.getTransactionFee().add(order.getFixedFee()));
        
        return save(order);
    }
}
