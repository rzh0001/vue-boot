package org.jeecg.modules.api.extension.gm;

import org.jeecg.modules.api.extension.gm.GMPay;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.stereotype.Component;

@Component("ali_bank")
public class AliBankPay extends GMPay {

	private String payType = BaseConstant.REQUEST_ALI_BANK;
}
