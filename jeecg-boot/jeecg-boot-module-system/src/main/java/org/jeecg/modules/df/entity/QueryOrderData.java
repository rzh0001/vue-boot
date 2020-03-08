package org.jeecg.modules.df.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryOrderData extends ApiData {


	private String orderNo;

	@NotBlank(message = "bizOrderNo 不能为空")
	@Length(max = 32, message = "bizOrderNo 最大长度为32")
	private String bizOrderNo;


}
