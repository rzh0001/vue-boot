package org.jeecg.modules.pay.service.factory;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.pay.service.impl.OrderInfoEntityServiceImpl;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.pay.service.requestPayUrl.impl.AliPayImpl;
import org.jeecg.modules.pay.service.requestPayUrl.impl.YsfPayImpl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class PayServiceFactory {
    @Autowired
    public ISysDictService dictService;
    private static PayServiceFactory factory;
    private static String aliPayUrl = null;
    private static String bankPayUrl = null;
    private static String ysfPayUrl = null;
    private static String nxysWxPayUrl = null;
    private static String nxysAliPayUrl = null;


    @PostConstruct
    public void init() {
        factory = this;
        factory.dictService = this.dictService;
        List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
        for (DictModel dict : payUrl) {
            if (BaseConstant.REQUEST_ALI_ZZ.equals(dict.getText())) {
                aliPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_ALI_BANK.equals(dict.getText())) {
                bankPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_YSF.equals(dict.getText())) {
                ysfPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_NXYS_WX.equals(dict.getText())) {
                nxysWxPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_NXYS_ALIPAY.equals(dict.getText())) {
                nxysAliPayUrl = dict.getValue();
            }
        }

    }
    @Autowired
    public ApplicationContext applicationContext;

    public String getRequestUrl(String channel){
        switch (channel) {
            case BaseConstant.REQUEST_YSF:
                return ysfPayUrl;
            case BaseConstant.REQUEST_ALI_BANK:
                return bankPayUrl;
            case BaseConstant.REQUEST_ALI_ZZ:
                return aliPayUrl;
            default:
                return null;
        }
    }

    public RequestPayUrl getPay(String channel) {
        switch (channel) {
            case BaseConstant.REQUEST_YSF:
                return applicationContext.getBean(YsfPayImpl.class);
            case BaseConstant.REQUEST_ALI_BANK:
                return applicationContext.getBean(AliPayImpl.class);
            case BaseConstant.REQUEST_ALI_ZZ:
                return applicationContext.getBean(AliPayImpl.class);
            default:
                return null;
        }
    }
}
