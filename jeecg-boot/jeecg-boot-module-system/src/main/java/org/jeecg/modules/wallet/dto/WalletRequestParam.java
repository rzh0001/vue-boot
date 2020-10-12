package org.jeecg.modules.wallet.dto;

import lombok.Data;
import org.jeecg.modules.api.entity.ApiBase;
import org.jeecg.modules.api.exception.MissingRequiredParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Data
public class WalletRequestParam implements Serializable {
    /**
     * 外部订单号
     */
    @NotNull(message = "orderId不能为空")
    private String orderId;
    /**
     * 币种
     */
    @NotNull(message = "coinType不能为空")
    private Integer coinType;
    /**
     * 回调地址
     */
    @NotNull(message = "callUrl不能为空")
    private String callUrl;
    /**
     * 金额，精确到分
     *
     * 1元 = 100分
     */
    @NotNull(message = "amount不能为空")
    private Long amount;
    /**
     * 时间戳
     */
    @NotNull(message = "timestamp不能为空")
    private String timestamp;
    /**
     * remark：回调的时候原样返回
     */
    private String remark;
    /**
     * 签名：
     *
     * orderId=orderId&coinType=coinType&callUrl=callUrl&amount=amount&timestamp=timestamp&apiKey=apiKey
     */
    @NotNull(message = "sign不能为空")
    private String sign;


    public void checkData() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<WalletRequestParam>> validate = validator.validate(this);
        if (!validate.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            validate.forEach(item -> {
                builder.append("[").append(item.getMessage()).append("]").append(System.lineSeparator());
            });
            throw new MissingRequiredParameterException(builder.toString());
        }
    }
}
