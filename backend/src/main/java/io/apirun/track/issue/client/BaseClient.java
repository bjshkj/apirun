package io.apirun.track.issue.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.EncryptUtils;
import io.apirun.commons.utils.LogUtil;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public abstract class BaseClient {

    protected static RestTemplate restTemplate;

    static {
        try {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(csf)
                    .build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);
        } catch (Exception e) {
            LogUtil.error(e);
        }
    }

    protected static HttpHeaders getBasicHttpHeaders(String userName, String passWd) {
        String authKey = EncryptUtils.base64Encoding(userName + ":" + passWd);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + authKey);
        return headers;
    }

    protected static String getResult(ResponseEntity<String> response) {
        int statusCodeValue = response.getStatusCodeValue();
        LogUtil.info("responseCode: " + statusCodeValue);
        if(statusCodeValue >= 400){
            MSException.throwException(response.getBody());
        }
        LogUtil.info("result: " + response.getBody());
        return response.getBody();
    }

    protected static Object getResultForList(Class clazz, ResponseEntity<String> response) {
        return Arrays.asList(JSONArray.parseArray(getResult(response), clazz).toArray());
    }

    protected static Object getResultForObject(Class clazz,ResponseEntity<String> response) {
        return JSONObject.parseObject(getResult(response), clazz);
    }
}
