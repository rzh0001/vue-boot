package org.jeecg.modules.api.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-06-11 14:53
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreaterOrderParam implements Serializable {
    /**
     * 商户号
     */
    @NotBlank(message = "商户号pay_memberid不能为空")
    @JSONField(name="pay_memberid")
    private String payMenberid;

    /**
     * 订单号
     */
    @JSONField(name="pay_orderid")
    private String payOrderId;

    /**
     * 提交时间
     */
    @JSONField(name="pay_applydate")
    private Date payApplyDate;

    /**
     * 银行编码
     */
    @NotBlank(message = "银行编码pay_bankcode不能为空")
    @JSONField(name="pay_bankcode")
    private String payBankCode;

    /**
     * 服务端通知
     */
    @JSONField(name="pay_notifyurl")
    private String payNotifyUrl;

    /**
     * 页面跳转通知
     */
    @JSONField(name="pay_callbackurl")
    private String payCallbackUrl;

    /**
     * 订单金额
     */
    @NotBlank(message = "订单金额不能为空")
    @JSONField(name="pay_amount")
    private Double payAmount;

    /**
     * MD5签名
     */
    @JSONField(name="pay_md5sign")
    private String payMD5Sign;

    /**
     * 附加字段
     */
    @JSONField(name="pay_attach")
    private String payAttach;

    /**
     * 商品名称
     */
    @JSONField(name="pay_productname")
    private String payProductName;

    /**
     * 商户品数量
     */
    @JSONField(name="pay_productnum")
    private Integer payProductNum;

    /**
     * 商品描述
     */
    @JSONField(name="pay_productdesc")
    private String payProductDesc;

    /**
     * 商户链接地址
     */
    @JSONField(name="pay_producturl")
    private String payProductUrl;
}
