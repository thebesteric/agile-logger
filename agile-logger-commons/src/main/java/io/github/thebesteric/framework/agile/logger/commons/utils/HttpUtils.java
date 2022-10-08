package io.github.thebesteric.framework.agile.logger.commons.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpUtils
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2021-05-29 00:49
 * @since 1.0
 */
@Slf4j
public class HttpUtils {

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static ConnectionKeepAliveStrategy keepAliveStrategy;

    private static CloseableHttpClient sslHttpClient;
    private static CloseableHttpClient httpClient;

    private HttpUtils() {
        initPool(HttpUtilsConfig.builder().build());
    }

    public HttpUtils(HttpUtilsConfig config) {
        initPool(config);
    }

    public static HttpUtils getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final HttpUtils instance = new HttpUtils();
    }

    @Builder
    public static class HttpUtilsConfig {
        @Builder.Default
        private final int maxTotal = 200;
        @Builder.Default
        private final int maxPreRoute = 50;
        @Builder.Default
        private final int connectTimeout = 10000;
        @Builder.Default
        private final int socketTimeout = 10000;
        @Builder.Default
        private final int connectionRequestTimeout = 5000;
        @Builder.Default
        private final int validateAfterInactivity = 30000;
    }

    private void initPool(HttpUtilsConfig config) {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置整个连接池最大连接数
        connMgr.setMaxTotal(config.maxTotal);
        // 设置每个主机地址的并发数
        connMgr.setDefaultMaxPerRoute(config.maxPreRoute);
        // 设置不活动的的时间
        connMgr.setValidateAfterInactivity(config.validateAfterInactivity);

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(config.connectTimeout);
        // 设置读取超时
        configBuilder.setSocketTimeout(config.socketTimeout);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(config.connectionRequestTimeout);

        requestConfig = configBuilder.build();

        keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && "timeout".equalsIgnoreCase(param)) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return 5 * 1000;
        };
    }

    /**
     * 获取 CloseableHttpClient
     *
     * @param url       URL
     * @param keepAlive keepAlive
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getCloseableHttpClient(String url, boolean keepAlive) {
        if (url.startsWith("https")) {
            if (sslHttpClient == null) {
                HttpClientBuilder httpClientBuilder = HttpClients.custom().setSSLSocketFactory(createSslConnSocketFactory())
                        .setConnectionManager(connMgr).setConnectionManagerShared(true).setDefaultRequestConfig(requestConfig);
                if (keepAlive) {
                    httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
                }
                sslHttpClient = httpClientBuilder.build();
            }
            return sslHttpClient;
        } else {
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connMgr).setConnectionManagerShared(true)
                    .setDefaultRequestConfig(requestConfig);
            if (httpClient == null) {
                if (keepAlive) {
                    httpClientBuilder.setKeepAliveStrategy(keepAliveStrategy);
                }
            }
            httpClient = httpClientBuilder.build();
            return httpClient;
        }
    }

    /**
     * 获取 CloseableHttpClient
     *
     * @param url url
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getCloseableHttpClient(String url) {
        return getCloseableHttpClient(url, true);
    }


    /**
     * 发送 GET 请求
     *
     * @param url url
     */
    public ResponseEntry doGet(String url) {
        return doGet(url, new HashMap<>(16), null);
    }

    /**
     * 发送 GET 请求
     *
     * @param url     url
     * @param params  params
     * @param headers headers
     * @return ResponseEntry
     */
    public ResponseEntry doGet(String url, Map<String, Object> params, Map<String, String> headers) {
        return doGet(url, params, headers, true);
    }

    /**
     * 发送 GET 请求
     *
     * @param url       url
     * @param params    params
     * @param headers   headers
     * @param keepAlive keepAlive
     * @return ResponseEntry
     */
    public ResponseEntry doGet(String url, Map<String, Object> params, Map<String, String> headers, boolean keepAlive) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        if (params != null) {
            int i = 0;
            for (String key : params.keySet()) {
                if (i == 0) {
                    param.append("?");
                } else {
                    param.append("&");
                }
                param.append(key).append("=").append(params.get(key));
                i++;
            }
        }
        apiUrl += param;
        String httpStr = null;
        int statusCode = 0;
        CloseableHttpClient httpClient = getCloseableHttpClient(apiUrl, keepAlive);

        HttpGet httpGet = new HttpGet(apiUrl);
        httpGet.setConfig(requestConfig);
        try {
            if (headers != null && headers.size() > 0) {
                headers.forEach(httpGet::setHeader);
            }

            HttpResponse response = httpClient.execute(httpGet);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent()) {
                    httpStr = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                httpGet.releaseConnection();
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntry(statusCode, httpStr);
    }

    /**
     * 发送 POST 请求
     *
     * @param url url
     * @return JSONObject
     */
    public ResponseEntry doPost(String url) {
        return doPost(url, new HashMap<>(), new HashMap<>());
    }

    /**
     * 发送 POST 请求
     *
     * @param url       url
     * @param keepAlive keepAlive
     * @return JSONObject
     */
    public ResponseEntry doPost(String url, boolean keepAlive) {
        return doPost(url, new HashMap<>(), new HashMap<>(), keepAlive);
    }

    /**
     * 发送 POST 请求
     *
     * @param url     url
     * @param params  params
     * @param headers headers
     */
    public ResponseEntry doPost(String url, Map<String, Object> params, Map<String, String> headers) {
        return doPost(url, params, headers, true);
    }

    /**
     * 发送 POST 请求
     *
     * @param url       url
     * @param params    params
     * @param headers   headers
     * @param keepAlive keepAlive
     */
    public ResponseEntry doPost(String url, Map<String, Object> params, Map<String, String> headers, boolean keepAlive) {
        CloseableHttpClient httpClient = getCloseableHttpClient(url, keepAlive);
        String httpStr = null;
        int statusCode = 0;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;

        if (params == null) {
            params = new HashMap<>();
        }

        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, StandardCharsets.UTF_8));

            if (headers != null && headers.size() > 0) {
                headers.forEach(httpPost::setHeader);
            }

            response = httpClient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            httpPost.releaseConnection();
            if (null != response) {
                try {
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    httpClient.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return new ResponseEntry(statusCode, httpStr);
    }

    /**
     * 创建SSL安全连接
     *
     * @return SSLConnectionSocketFactory
     */
    private SSLConnectionSocketFactory createSslConnSocketFactory() {
        SSLConnectionSocketFactory sslFactory = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true).build();
            sslFactory = new SSLConnectionSocketFactory(sslContext, (String arg0, SSLSession arg1) -> true);
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage(), e);
        }
        return sslFactory;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ResponseEntry {
        private int code;
        private String httpStr;
    }
}
