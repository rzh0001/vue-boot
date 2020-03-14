package org.jeecg.modules.df.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBankcardVo {

	/**
	 * 主键id
	 */
	private String id;
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户登录账号
	 */
	@Excel(name = "用户", width = 15)
	private String username;
	/**
	 * 账户类型(1-对私;2-对公)
	 */
	@Excel(name = "账户类型(1-对私;2-对公)", width = 15)
	private String accountType;
	/**
	 * 账户名
	 */
	@Excel(name = "账户名", width = 15)
	private String accountName;
	/**
	 * 卡号
	 */
	@Excel(name = "卡号", width = 15)
	private String cardNumber;
	/**
	 * 银行名称
	 */
	@Excel(name = "银行名称", width = 15)
	private String bankName;
	/**
	 * 开户行全称
	 */
	@Excel(name = "开户行全称", width = 15)
	private String branchName;
	/**
	 * 备注
	 */
	@Excel(name = "备注", width = 15)
	private String remark;
	/**
	 * 开关(0-关闭;1-开启)
	 */
	@Excel(name = "开关(0-关闭;1-开启)", width = 15)
	private String isOpen;

	@Excel(name = "今日充值", width = 15)
	private BigDecimal todayAmount;

}
