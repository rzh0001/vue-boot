package org.jeecg.modules.df.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2020-06-17
 * @Version: V1.0
 */
@Data
@TableName("df_user_bankcard_config")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "df_user_bankcard_config对象", description = "1")
public class UserBankcardConfig {

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private java.lang.String userId;
	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private java.lang.String userName;
	/**
	 * 代理ID
	 */
	@ApiModelProperty(value = "代理ID")
	private java.lang.String agentId;
	/**
	 * 银行卡ID
	 */
	@ApiModelProperty(value = "银行卡ID")
	private java.lang.String cardId;
	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**
	 * 创建时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;
	/**
	 * 主键id
	 */
	@TableId(type = IdType.UUID)
	@ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**
	 * 最后使用时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "最后使用时间")
	private java.util.Date lastTime;
	/**
	 * 更新人
	 */
	@ApiModelProperty(value = "更新人")
	private java.lang.String updateBy;
	/**
	 * 更新时间
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "更新时间")
	private java.util.Date updateTime;
	/**
	 * 乐观锁
	 */
	@ApiModelProperty(value = "乐观锁")
	private java.lang.Integer version;
}
