package org.jeecg.modules.v2.entity;

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
 * @Description: 用户关联产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Data
@TableName("pay_v2_user_product")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_v2_user_product对象", description="用户关联产品")
public class PayUserProduct {
    
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
	/**id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "id")
	private java.lang.String id;
	/**会员类型(1：代理  2：介绍人 3：商户）*/
	@Excel(name = "会员类型(1：代理  2：介绍人 3：商户）", width = 15)
    @ApiModelProperty(value = "会员类型(1：代理  2：介绍人 3：商户）")
	private java.lang.String memberType;
	/**产品代码*/
	@Excel(name = "产品代码", width = 15)
    @ApiModelProperty(value = "产品代码")
	private java.lang.String productCode;
	/**状态 0：关闭；1：开启*/
	@Excel(name = "状态 0：关闭；1：开启", width = 15)
    @ApiModelProperty(value = "状态 0：关闭；1：开启")
	private java.lang.Integer status;
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
	/**用户账号*/
	@Excel(name = "用户账号", width = 15)
    @ApiModelProperty(value = "用户账号")
	private java.lang.String username;
}
