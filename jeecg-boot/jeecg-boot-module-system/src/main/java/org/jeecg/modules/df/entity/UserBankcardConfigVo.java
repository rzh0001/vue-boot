package org.jeecg.modules.df.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserBankcardConfigVo {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 用户登录账号
	 */
	private String username;
	private String userId;

	/**
	 * 开关(0-关闭;1-开启)
	 */
	private String status;

}
