package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    private String password;

    /**
     * md5密码盐
     */
    private String salt;

    /**
     * 头像
     */
    @Excel(name = "头像", width = 15)
    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private Integer sex;

    /**
     * 电子邮件
     */
    @Excel(name = "电子邮件", width = 15)
    private String email;

    /**
     * 电话
     */
    @Excel(name = "电话", width = 15)
    private String phone;

    /**
     * 部门code
     */
    private String orgCode;

    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Excel(name = "状态", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private Integer status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @Excel(name = "删除状态", width = 15,dicCode="del_flag")
    @TableLogic
    private String delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 同步工作流引擎1同步0不同步
     */
    private String activitiSync;
    
    /**会员类型(1：代理  2：介绍人 3：商户）*/
    @Excel(name = "会员类型(1：代理  2：介绍人 3：商户）", width = 15)
    @ApiModelProperty(value = "会员类型(1：代理  2：介绍人 3：商户）")
    private java.lang.String memberType;
    /**单笔金额上限*/
    @Excel(name = "单笔金额上限", width = 15)
    @ApiModelProperty(value = "单笔金额上限")
    private java.math.BigDecimal upperLimit;
    /**单笔金额下限*/
    @Excel(name = "单笔金额下限", width = 15)
    @ApiModelProperty(value = "单笔金额下限")
    private java.math.BigDecimal lowerLimit;
    /**代理ID*/
    @Excel(name = "代理ID", width = 15)
    @ApiModelProperty(value = "代理ID")
    private java.lang.String agentId;
    /**代理帐号*/
    @Excel(name = "代理帐号", width = 15)
    @ApiModelProperty(value = "代理帐号")
    private java.lang.String agentUsername;
    /**代理姓名*/
    @Excel(name = "代理姓名", width = 15)
    @ApiModelProperty(value = "代理姓名")
    private java.lang.String agentRealname;
    /**介绍人ID*/
    @Excel(name = "介绍人ID", width = 15)
    @ApiModelProperty(value = "介绍人ID")
    private java.lang.String salesmanId;
    /**介绍人帐号*/
    @Excel(name = "介绍人帐号", width = 15)
    @ApiModelProperty(value = "介绍人帐号")
    private java.lang.String salesmanUsername;
    /**介绍人姓名*/
    @Excel(name = "介绍人姓名", width = 15)
    @ApiModelProperty(value = "介绍人姓名")
    private java.lang.String salesmanRealname;
    /**谷歌密钥*/
    @Excel(name = "谷歌密钥", width = 15)
    @ApiModelProperty(value = "谷歌密钥")
    private java.lang.String googleSecretKey;
    /**支付密码*/
    @Excel(name = "支付密码", width = 15)
    @ApiModelProperty(value = "支付密码")
    private java.lang.String paymentPassword;
    /**IP白名单开关*/
    @Excel(name = "IP白名单开关", width = 15)
    @ApiModelProperty(value = "IP白名单开关")
    private java.lang.String ipSwitch;

}
