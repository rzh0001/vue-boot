package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 用户表
 * @Author: jeecg-boot
 * @Date:   2019-08-02
 * @Version: V1.0
 */
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_user对象", description="用户表")
public class SysUser {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**登录账号*/
	@Excel(name = "登录账号", width = 15)
    @ApiModelProperty(value = "登录账号")
	private java.lang.String username;
	/**真实姓名*/
	@Excel(name = "真实姓名", width = 15)
    @ApiModelProperty(value = "真实姓名")
	private java.lang.String realname;
	/**密码*/
    @ApiModelProperty(value = "密码")
	private java.lang.String password;
	/**密码*/
	@ApiModelProperty(value = "api密钥")
	private java.lang.String apiKey;
	/**md5密码盐*/
    @ApiModelProperty(value = "md5密码盐")
	private java.lang.String salt;
	/**头像*/
	@Excel(name = "头像", width = 15)
    @ApiModelProperty(value = "头像")
	private java.lang.String avatar;
	/**生日*/
	@Excel(name = "生日", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "生日")
	private java.util.Date birthday;
	/**性别（1：男 2：女）*/
	@Excel(name = "性别（1：男 2：女）", width = 15)
    @ApiModelProperty(value = "性别（1：男 2：女）")
	private java.lang.Integer sex;
	/**电子邮件*/
	@Excel(name = "电子邮件", width = 15)
    @ApiModelProperty(value = "电子邮件")
	private java.lang.String email;
	/**电话*/
	@Excel(name = "电话", width = 15)
    @ApiModelProperty(value = "电话")
	private java.lang.String phone;
	/**部门code*/
	@Excel(name = "部门code", width = 15)
    @ApiModelProperty(value = "部门code")
	private java.lang.String orgCode;
	/**状态(1：正常  2：冻结 ）*/
	@Excel(name = "状态(1：正常  2：冻结 ）", width = 15)
    @ApiModelProperty(value = "状态(1：正常  2：冻结 ）")
	@Dict(dicCode = "user_status")
	private java.lang.Integer status;
	/**删除状态（0，正常，1已删除）*/
	@Excel(name = "删除状态（0，正常，1已删除）", width = 15)
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
	private java.lang.String delFlag;
	/**同步工作流引擎1同步0不同步*/
	@Excel(name = "同步工作流引擎1同步0不同步", width = 15)
    @ApiModelProperty(value = "同步工作流引擎1同步0不同步")
	private java.lang.String activitiSync;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**更新人*/
	@Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**更新时间*/
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
	private java.util.Date updateTime;
	
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
    @ApiModelProperty(value = "谷歌密钥")
	private java.lang.String googleSecretKey;
	/**支付密码*/
	@ApiModelProperty(value = "支付密码")
	private java.lang.String paymentPassword;
	/**IP白名单开关*/
	@Excel(name = "IP白名单开关", width = 15)
    @ApiModelProperty(value = "IP白名单开关")
	private java.lang.String ipSwitch;
	/**
	 * 订单单笔手续费
	 */
	@Excel(name = "订单单笔手续费", width = 15)
	@ApiModelProperty(value = "订单单笔手续费")
	private java.math.BigDecimal orderFixedFee;
	/**
	 * 交易手续费率
	 */
	@Excel(name = "交易手续费率", width = 15)
	@ApiModelProperty(value = "交易手续费率")
	private java.math.BigDecimal transactionFeeRate;
}
