package org.jeecg.modules.pay.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2019-08-13
 * @Version: V1.0
 */
@Data
@TableName("pay_member_bank_card")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_member_bank_card对象", description="1")
public class BankCard {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**用户ID*/
	@Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
	private java.lang.String userId;
	/**用户登录账号*/
	@Excel(name = "用户登录账号", width = 15)
    @ApiModelProperty(value = "用户登录账号")
	private java.lang.String username;
	/**银行名称*/
	@Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
	private java.lang.String bankName;
	/**分支行*/
	@Excel(name = "分支行", width = 15)
    @ApiModelProperty(value = "分支行")
	private java.lang.String branchName;
	/**账户名*/
	@Excel(name = "账户名", width = 15)
    @ApiModelProperty(value = "账户名")
	private java.lang.String accountName;
	/**卡号*/
	@Excel(name = "卡号", width = 15)
    @ApiModelProperty(value = "卡号")
	private java.lang.String cardNumber;
	/**删除状态（0，正常，1已删除）*/
	@Excel(name = "删除状态（0，正常，1已删除）", width = 15)
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
	private java.lang.String delFlag;
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
	/**默认卡(0-否;1-是)*/
	@Excel(name = "默认卡(0-否;1-是)", width = 15)
    @ApiModelProperty(value = "默认卡(0-否;1-是)")
	private java.lang.String isDefault;
}
