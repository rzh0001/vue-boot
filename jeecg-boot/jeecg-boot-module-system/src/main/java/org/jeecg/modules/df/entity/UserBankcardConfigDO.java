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

/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBankcardConfigDO {

	private String id;
	private String userId;
	private String agentId;
	private String cardId;
	/**
	 * 账户类型(1-对私;2-对公)
	 */
	private String accountType;
	/**
	 * 账户名
	 */
	private String accountName;
	/**
	 * 卡号
	 */
	private String cardNumber;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 开户行全称
	 */
	private String branchName;
	/**
	 * 备注
	 */
	private String remark;

}
