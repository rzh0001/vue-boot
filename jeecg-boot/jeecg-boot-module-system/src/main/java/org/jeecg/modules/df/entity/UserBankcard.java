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
@TableName("df_user_bankcard")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "df_user_bankcard对象", description = "代付平台用户银行卡")
public class UserBankcard {
    
    /**
     * 主键id
     */
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;
    /**
     * 用户ID
     */
    @Excel(name = "用户ID", width = 15)
    @ApiModelProperty(value = "用户ID")
    private java.lang.String userId;
    /**
     * 用户登录账号
     */
    @Excel(name = "用户登录账号", width = 15)
    @ApiModelProperty(value = "用户登录账号")
    private java.lang.String username;
    /**
     * 账户类型(1-对私;2-对公)
     */
    @Excel(name = "账户类型(1-对私;2-对公)", width = 15)
    @ApiModelProperty(value = "账户类型(1-对私;2-对公)")
    private java.lang.String accountType;
    /**
     * 账户名
     */
    @Excel(name = "账户名", width = 15)
    @ApiModelProperty(value = "账户名")
    private java.lang.String accountName;
    /**
     * 卡号
     */
    @Excel(name = "卡号", width = 15)
    @ApiModelProperty(value = "卡号")
    private java.lang.String cardNumber;
    /**
     * 银行名称
     */
    @Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
    private java.lang.String bankName;
    /**
     * 开户行全称
     */
    @Excel(name = "开户行全称", width = 15)
    @ApiModelProperty(value = "开户行全称")
    private java.lang.String branchName;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 开关(0-关闭;1-开启)
     */
    @Excel(name = "开关(0-关闭;1-开启)", width = 15)
    @ApiModelProperty(value = "开关(0-关闭;1-开启)")
    private java.lang.String isOpen;
    /**
     * 默认卡(0-否;1-是)
     */
    @Excel(name = "默认卡(0-否;1-是)", width = 15)
    @ApiModelProperty(value = "默认卡(0-否;1-是)")
    private java.lang.String isDefault;
    /**
     * 删除状态(0-正常;1-已删除)
     */
    @Excel(name = "删除状态(0-正常;1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常;1-已删除)")
    private java.lang.String delFlag;
    /**
     * 创建人
     */
    @Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建时间
     */
    @Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @Excel(name = "更新人", width = 15)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    /**
     * 乐观锁
     */
    @Excel(name = "乐观锁", width = 15)
    @ApiModelProperty(value = "乐观锁")
    @Version
    private java.lang.Integer version;
}
