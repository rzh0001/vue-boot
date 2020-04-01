package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.SignatureUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.cert.CertificateException;
import java.util.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/30 19:10
 */
@Service
@Slf4j
public class LeTianPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity,
    Object>, InitializingBean, ApplicationContextAware {
    private static final String CALLBACK_URL = "/callBack/leTianAlipay";
    @Autowired
    public ISysDictService dictService;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        Map<String, String> paraMap = new HashMap();
        paraMap.put("outTradeNo",order.getOrderId() );
        paraMap.put("totalAmount", order.getSubmitAmount().toString());
        //登录后台  推广商管理  推广商   推广商ID
        paraMap.put("promoterId", userBusiness.getBusinessCode());
        paraMap.put("noticeUrl", this.getDomain()+CALLBACK_URL);
        paraMap.put("tradeType", "0");
        //文档中X-Signature值
        String secret = userBusiness.getApiKey();
        log.info("==>乐天娱乐 ，请求入参：{}",paraMap);
        String signature = SignatureUtils.signature(secret, paraMap);
        Map<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("X-Signature", signature);
        //文档中X-ClientId值
        headerMap.put("X-ClientId", "0d69af53d2d3334a9d296ca47d8f0b2");
        log.info("==>乐天娱乐，header:{}",headerMap);
        String body = this.send(url, paraMap, "utf-8",headerMap);
        log.info("==>乐天娱乐，请求响应：{}",body);
        redisUtil.del(order.getOuterOrderId());
        JSONObject result = JSON.parseObject(body);
        return R.ok().put("url", result.get("pay_url"));
    }

    private String getDomain(){
        String domain = null;
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.DOMAIN);
        for (DictModel k : apiKey) {
            if (BaseConstant.DOMAIN.equals(k.getText())) {
                domain = k.getValue();
            }
        }
        return domain;
    }
    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
        throws Exception {
        Map<String, String> paraMap = new HashMap();
        paraMap.put("outTradeNo", order.getOrderId());
        paraMap.put("promoterId", userBusiness.getBusinessCode());
        String secret = userBusiness.getApiKey();
        String signature = SignatureUtils.signature(secret, paraMap);

        Map<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("X-Signature", signature);
        headerMap.put("X-ClientId", "0d69af53d2df1f24a9d296ca47d8f0b1");
        String body = this.send(url, paraMap, "utf-8",headerMap);
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_LETIAN_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_LETIAN_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_LETIAN_ALIPAY));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
    public SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("SSLv3");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(
                java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }
    /**
     * 模拟请求
     *
     * @param url		资源地址
     * @param paraMap	参数列表
     * @param encoding	编码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public String send(String url, Map<String,String> paraMap,String encoding, Map<String,String> headerMap) throws KeyManagementException, NoSuchAlgorithmException, ClientProtocolException, IOException {
        String body = "";

        SSLContext sslcontext = createIgnoreVerifySSL();

        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        //创建自定义的httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();

        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(paraMap!=null){
            for (Entry<String, String> entry : paraMap.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

        System.out.println("请求地址："+url);
        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpPost.setHeader("X-Signature",headerMap.get("X-Signature"));
        httpPost.setHeader("X-ClientId",headerMap.get("X-ClientId"));

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
    }



}
