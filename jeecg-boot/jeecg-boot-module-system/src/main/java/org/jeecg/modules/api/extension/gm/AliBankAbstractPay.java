package org.jeecg.modules.api.extension.gm;

import org.jeecg.modules.util.BaseConstant;
import org.springframework.stereotype.Component;

@Component("ali_bank")
public class AliBankAbstractPay extends GMAPay {

	private String payType = BaseConstant.REQUEST_ALI_BANK;
}
