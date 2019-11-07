package org.jeecg.modules.system.vo;

import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

/**
 * @Description: 用户表
 * @Author: jeecg-boot
 * @Date: 2019-08-02
 * @Version: V1.0
 */
@Data
public class SysUserPage {
    
    /**
     * 主键id
     */
    private String id;
    /**
     * 登录账号
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realname;
    /**
     * api密钥
     */
    private String apiKey;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 生日
     */
    private java.util.Date birthday;
    /**
     * 性别（1：男 2：女）
     */
    private Integer sex;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 部门code
     */
    private String orgCode;
    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Dict(dicCode = "user_status")
    private Integer status;
    /**
     * 删除状态（0，正常，1已删除）
     */
    private String delFlag;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private String activitiSync;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private java.util.Date createTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private java.util.Date updateTime;
    
    /**
     * 会员类型(1：代理  2：介绍人 3：商户）
     */
    private String memberType;
    /**
     * 单笔金额上限
     */
    private java.math.BigDecimal upperLimit;
    /**
     * 单笔金额下限
     */
    private java.math.BigDecimal lowerLimit;
    /**
     * 代理ID
     */
    private String agentId;
    /**
     * 代理帐号
     */
    private String agentUsername;
    /**
     * 代理姓名
     */
    private String agentRealname;
    /**
     * 介绍人ID
     */
    private String salesmanId;
    /**
     * 介绍人帐号
     */
    private String salesmanUsername;
    /**
     * 介绍人姓名
     */
    private String salesmanRealname;
    /**
     * 谷歌密钥
     */
    private String googleSecretKey;
    /**
     * 支付密码
     */
    private String paymentPassword;
    /**
     * IP白名单开关
     */
    private String ipSwitch;
    /**
     * 会员余额
     */
    private java.math.BigDecimal amount;
    /**
     * 订单单笔手续费
     */
    private java.math.BigDecimal orderFixedFee;
    /**
     * 交易手续费率
     */
    private java.math.BigDecimal transactionFeeRate;
}
