package org.jeecg.modules.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
	//设置连接参数
    private static RequestConfig config = RequestConfig.custom()
										   .setConnectionRequestTimeout(500)
								           .setSocketTimeout(30000) //设置读取超时
								           .setConnectTimeout(5000) // 设置连接超时
								           .build();
    
    private static CloseableHttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException {
        return HttpClientBuilder
        		.create()
        		.setMaxConnTotal(200)
        		.setMaxConnPerRoute(100)
                .setConnectionManager(HttpUtils.getPoolingHttpClientConnectionManager())
        		.build();
    }

    private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager()
        throws KeyManagementException, NoSuchAlgorithmException {
        //采用绕过验证的方式处理https请求
        SSLContext sslcontext = createIgnoreVerifySSL();

        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
            .register("http", PlainConnectionSocketFactory.INSTANCE)
            .register("https", new SSLConnectionSocketFactory(sslcontext))
            .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return connManager;
    }
    /**
     * 绕过验证
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sc = SSLContext.getInstance("TLS");

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
     * 
     * @return 响应体的内容
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static String doGet(String url)
        throws ClientProtocolException, IOException, KeyManagementException, NoSuchAlgorithmException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);//设置请求参数
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            // 执行请求
            response = httpClient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//                System.out.println("内容长度："+content.length());
                return content;
            }
        } finally {
            if (response != null) {
                response.close();
            }
            if(httpClient!=null){
                httpClient.close();
            }
        }
        return null;
    }

    /**
     * 带有参数的get请求
     * @param url
     * @return
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static String doGet(String url , Map<String, String> params)
        throws URISyntaxException, ClientProtocolException, IOException, NoSuchAlgorithmException,
        KeyManagementException {
        URIBuilder uriBuilder = new URIBuilder(url);
        if(params != null){
            for(String key : params.keySet()){
                uriBuilder.setParameter(key, params.get(key));
            }
        }//http://xxx?ss=ss
        return doGet(uriBuilder.build().toString());
    }
    
    /**
     * 没有参数的post请求
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static HttpResult doPost(String url) throws Exception {
        return doPost(url, null);
    }

    public static void main(String[] args) throws Exception {
        String url = "https://pay3.sg123.ws/return/158268993244823522";
        HttpUtils.doPost(url);
    }
    /**
     * 带有参数的post请求
     * @param url
     * @param params
     * @return
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static HttpResult doPost(String url , Map<String, Object> params) throws Exception {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        if(params != null){
            // 设置2个post参数，一个是scope、一个是q
            List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
            for(String key : params.keySet()){
                parameters.add(new BasicNameValuePair(key, params.get(key)+""));
            }
            // 构造一个form表单式的实体
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(formEntity);
        }
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            /*if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }*/
            return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
            if(httpClient!=null){
                httpClient.close();
            }
        }
    }

    public static HttpResult doPostJson(String url , String json) throws Exception {
         // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(config);
        if(StringUtils.isNotBlank(json)){
            //标识出传递的参数是 application/json
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
        }
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            // 执行请求
            response = httpClient.execute(httpPost);
            // 判断返回状态是否为200
            /*if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println(content);
            }*/
            return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
            //httpClient.close();
        }
    }

    

}
