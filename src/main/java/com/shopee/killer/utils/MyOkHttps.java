package com.shopee.killer.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Slf4j
public class MyOkHttps {
    private static volatile MyOkHttps myOkHttps;

    private OkHttpClient okHttpClient = null;

    static final MediaType JSON_TYPE = MediaType.parse("application/json");

    private MyOkHttps() {

    }

    public static synchronized MyOkHttps getInstance() throws UnrecoverableKeyException, CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        if (myOkHttps == null) {
            myOkHttps = new MyOkHttps();
        }
        myOkHttps.initOkHttpClient();
        return myOkHttps;
    }

    public MyOkHttps initOkHttpClient() throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        OkHttpClient.Builder builder;
        if (this.okHttpClient == null) {
            builder = new OkHttpClient.Builder();
        } else {
            builder = okHttpClient.newBuilder();
        }

        ClassPathResource classPathResource = new ClassPathResource("mall_shopee_sg.cer");
        InputStream inputStream = classPathResource.getInputStream();
        X509TrustManager x509TrustManager = trustManagerForCertificates(inputStream);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        this.okHttpClient = builder
                .connectionPool(new ConnectionPool(200, 10, TimeUnit.SECONDS))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
//                .sslSocketFactory(SSLSocketManager.getSSLSocketFactory(), SSLSocketManager.getTrustManager())
//                .hostnameVerifier(SSLSocketManager.getHostnameVerifier())
//                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
//                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                .sslSocketFactory(sslSocketFactory, x509TrustManager)
                .addInterceptor(new GzipInterceptor())
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                .build();
        return this;
    }

    /**
     * @description: 信任指定证书
     * @param: [certificates]
     * @return: javax.net.ssl.SSLSocketFactory
     * @date: 2023/12/7 4:06
     */
    private static X509TrustManager trustManagerForCertificates(InputStream in) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        char[] password = "password".toCharArray();
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // 使用包含信任证书的密钥库初始化一个密钥管理器
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    /**
     * 创建一个空的证书库
     * @param password
     * @return
     */
    private static KeyStore newEmptyKeyStore(char[] password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, password);
            return keyStore;
        } catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @description: 发送get请求
     * @param: [url, params]
     * @return: java.lang.String
     */
    public String doGet(String url, Map<String, Object> params, Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(UrlBuilder.ofHttp(url).build())).newBuilder();
        if (MapUtil.isNotEmpty(params)) {
            params.forEach((k, v) -> urlBuilder.addQueryParameter(k, v.toString()));
        }

        Request.Builder request = new Request.Builder();
        request.url(urlBuilder.build());
        if (MapUtil.isNotEmpty(headers)) {
            request.headers(Headers.of(headers));
        }

        Response response = RetryFunction.retry(5, 1, () -> {
            try {
                return okHttpClient.newCall(request.build()).execute();
            } catch (IOException e) {
                throw new RuntimeException("http get请求执行异常，url=" + url, e);
            }
        });

        return checkResponse(response);
    }

    /**
     * @description: 发送post请求
     * @param: [url, params]
     * @return: java.lang.String
     */
    public String doPost(String url, Map<String, Object> params) {
        RequestBody requestBody = RequestBody.create(JSON_TYPE, JSONUtil.toJsonStr(params));
        Request.Builder request = new Request.Builder();
        request.url(url).post(requestBody);

        Response response = RetryFunction.retry(5, 1, () -> {
            try {
                return okHttpClient.newCall(request.build()).execute();
            } catch (IOException e) {
                throw new RuntimeException("http post请求执行异常，url=" + url, e);
            }
        });

        return checkResponse(response);
    }

    /**
     * 简单处理响应
     * @param response
     * @return
     */
    private String checkResponse(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new HttpRequestException("http结果解析异常,解析内容为：" + response, e);
        }
    }

    /**
     * @author Administrator
     * @version 1.0
     * @description: gzip 解压缩拦截器
     */
    public static class GzipInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if ("gzip".equalsIgnoreCase(originalResponse.header("content-encoding"))) {
                // 解压缩响应体
                ResponseBody responseBody = ResponseBody.create(
                        unzip(originalResponse.body().bytes()),
                        originalResponse.body().contentType()
                );
                return originalResponse.newBuilder()
                        .body(responseBody)
                        .build();
            } else {
                return originalResponse;
            }
        }

        private byte[] unzip(byte[] compressed) throws IOException {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(new java.io.ByteArrayInputStream(compressed))) {
                return readFully(gzipInputStream);
            }
        }

        private byte[] readFully(GZIPInputStream inputStream) throws IOException {
            // 以缓冲方式读取解压缩后的内容
            byte[] buffer = new byte[1024];
            int bytesRead;
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        }
    }
}
