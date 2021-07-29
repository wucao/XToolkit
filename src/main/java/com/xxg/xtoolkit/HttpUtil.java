package com.xxg.xtoolkit;

import okhttp3.*;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class HttpUtil {

    private static final OkHttpClient defaultOkHttpClient = new OkHttpClient();;

    /**
     * GET 请求
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * GET 请求（带参数）
     */
    public static String get(String url, Map<String, String> queryParameters) throws IOException {
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
        try (Response response = defaultOkHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + httpUrl, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 form 表单）
     */
    public static String postForm(String url, Map<String, String> formData) throws IOException {

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
        try (Response response = defaultOkHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url + "，RequestBody：" + formData, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 json，带 header）
     */
    public static String postJson(String url, String requestBody, Map<String, String> requestHeaders) throws IOException {
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
        try (Response response = defaultOkHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url + "，RequestBody：" + requestBody, e);
        }
    }

    /**
     * POST 请求（请求 BODY 为 json）
     */
    public static String postJson(String url, String requestBody) throws IOException {
        return postJson(url, requestBody, null);
    }

    /**
     * GET 请求下载文件
     */
    public static void downloadFile(String url, File localFile) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = defaultOkHttpClient.newCall(request).execute()) {
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
    public static String uploadFile(String url, String fileParameterName, File file) throws IOException {
        return uploadFile(url, null, Collections.singletonMap(fileParameterName, file), null);
    }

    /**
     * POST multipart/form-data 上传文件
     */
    public static String uploadFile(String url, Map<String, String> bodyParameters, Map<String, File> files, Map<String, String> requestHeaders) throws IOException {

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
        try (Response response = defaultOkHttpClient.newCall(request).execute()) {
            checkSuccessful(response);
            return response.body().string();
        } catch (Exception e) {
            throw new IOException("请求异常，URL：" + url, e);
        }
    }

    /**
     * 检查状态码
     */
    private static void checkSuccessful(Response response) throws IOException {
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
}
