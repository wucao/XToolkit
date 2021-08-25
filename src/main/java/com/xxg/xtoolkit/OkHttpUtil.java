package com.xxg.xtoolkit;

import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class OkHttpUtil {

    private OkHttpClient okHttpClient = new OkHttpClient();

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * GET 请求
     */
    public String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * GET 请求（带参数）
     */
    public String get(String url, Map<String, String> queryParameters) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParameters != null) {
            for (Map.Entry<String, String> queryParameter : queryParameters.entrySet()) {
                urlBuilder.addQueryParameter(queryParameter.getKey(), queryParameter.getValue());
            }
        }
        HttpUrl httpUrl = urlBuilder.build();
        Request request = new Request.Builder()
                .url(httpUrl)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + httpUrl, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 form 表单）
     */
    public String postForm(String url, Map<String, String> formData) throws IOException {

        FormBody.Builder requestBodyBuilder = new FormBody.Builder();
        if (formData != null) {
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                requestBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = requestBodyBuilder.build();

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url + "，RequestBody：" + formData, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 json，带 header）
     */
    public String postJson(String url, String requestBody, Map<String, String> requestHeaders) throws IOException {
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (requestHeaders != null) {
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }
        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url + "，RequestBody：" + requestBody, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 json）
     */
    public String postJson(String url, String requestBody) throws IOException {
        return postJson(url, requestBody, null);
    }

    /**
     * GET 请求下载文件
     */
    public void downloadFile(String url, File localFile) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            try (BufferedSink sink = Okio.buffer(Okio.sink(localFile))) {
                sink.writeAll(response.body().source());
            }
        } catch (Exception e) {
            throw new IOException("下载文件异常，URL：" + url, e);
        }
    }

    /**
     * POST multipart/form-data 上传单个文件
     */
    public String uploadFile(String url, String fileParameterName, File file) throws IOException {
        return uploadFile(url, null, Collections.singletonMap(fileParameterName, file), null);
    }

    /**
     * POST multipart/form-data 上传文件
     */
    public String uploadFile(String url, Map<String, String> bodyParameters, Map<String, File> files, Map<String, String> requestHeaders) throws IOException {

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (bodyParameters != null) {
            for (Map.Entry<String, String> bodyParameter : bodyParameters.entrySet()) {
                bodyBuilder.addFormDataPart(bodyParameter.getKey(), bodyParameter.getValue());
            }
        }
        if (files != null) {
            for (Map.Entry<String, File> fileItem : files.entrySet()) {
                bodyBuilder.addFormDataPart(fileItem.getKey(), fileItem.getValue().getName(),
                        RequestBody.create(fileItem.getValue(), MediaType.parse("application/octet-stream")));
            }
        }
        RequestBody body = bodyBuilder.build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);
        if (requestHeaders != null) {
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                builder.header(header.getKey(), header.getValue());
            }
        }
        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url, e);
        }
    }

    /**
     * 检查状态码
     */
    private void checkSuccessful(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String body = null;
            try {
                body = response.body().string();
            } catch (Exception e) {
                e.printStackTrace(); // 忽略异常，仅输出异常信息
            }

            if (body != null) {
                throw new IOException("Unexpected code: " + response + ", Response body: " + body);
            } else {
                throw new IOException("Unexpected code: " + response);
            }
        }
    }



    /**
     * 创建 TLS 双向认证（ mutual TLS authentication ）的 OkHttpClient
     *
     * @param pkcs12Certificate 传入证书文件的输入流，文件一般是 .p12/.pfx 格式
     * @param keyStorePassword 传入证书对应的密码，微信支付一般用商户号作为证书密码
     */
    public OkHttpClient createMutualTLSClient(InputStream pkcs12Certificate, char[] keyStorePassword) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, KeyManagementException {

        // 加载 keyStore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(pkcs12Certificate, keyStorePassword);

        // 创建密钥管理器
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);

        // 初始化 SSLContext
        // 参考： org.apache.http.ssl.SSLContexts.custom().loadKeyMaterial(keyStore, keyStorePassword).build()
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // 默认的 TrustManager
        // 参考 okhttp3.internal.Util.platformTrustManager()： https://github.com/square/okhttp/blob/parent-3.14.9/okhttp/src/main/java/okhttp3/internal/Util.java#L636
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }

        // 创建 OkHttpClient 客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                .build();

        return client;
    }
}
