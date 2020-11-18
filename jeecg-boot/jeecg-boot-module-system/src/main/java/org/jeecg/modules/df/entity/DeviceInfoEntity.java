package org.jeecg.modules.df.entity;

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
 * @Description: 设备信息
 * @Author: jeecg-boot
 * @Date:   2020-11-16
 * @Version: V1.0
 */
@Data
@TableName("pay_device_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="pay_device_info对象", description="设备信息")
public class DeviceInfoEntity {
    
	/**主键id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
	private java.lang.String id;
	/**设备名称*/
	@Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
	private java.lang.String deviceName;
	/**设备编码*/
	@Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
	private java.lang.String deviceCode;
	/**秘钥*/
	@Excel(name = "秘钥", width = 15)
    @ApiModelProperty(value = "秘钥")
	private java.lang.String key;
	/**限额*/
	@Excel(name = "限额", width = 15)
    @ApiModelProperty(value = "限额")
	private java.lang.Integer limit;
	/**余额*/
	@Excel(name = "余额", width = 15)
    @ApiModelProperty(value = "余额")
	private java.lang.Integer balance;
	/**分组编码*/
	@Excel(name = "分组编码", width = 15)
    @ApiModelProperty(value = "分组编码")
	private java.lang.String groupingCode;
	/**状态：1：正常；2：禁用*/
	@Excel(name = "状态：1：正常；2：禁用", width = 15)
    @ApiModelProperty(value = "状态：1：正常；2：禁用")
	private java.lang.Integer status;
	/**清零时间*/
	@Excel(name = "清零时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "清零时间")
	private java.util.Date clearedTime;
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
}
