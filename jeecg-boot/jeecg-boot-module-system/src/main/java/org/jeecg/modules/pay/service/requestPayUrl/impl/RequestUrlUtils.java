package org.jeecg.modules.pay.service.requestPayUrl.impl;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestUrlUtils {
    @Autowired
    public ISysDictService dictService;

    /**
     * 从数据字典中获取请求四方的url
     *
     * @param payType
     * @return
     */
    public String getRequestUrl(String payType) {
        List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
        Optional<DictModel> urlModel = payUrl.stream().filter(model -> payType.equals(model.getText())).findFirst();
        if (urlModel.isPresent()) {
            DictModel model = urlModel.get();
            return model.getValue();
        }
        return "";
    }
}
