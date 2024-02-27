package com.example.demo.meetphoto.data.http;

import com.example.demo.meetphoto.data.model.ConfigC;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Des 添加请求参数拦截器
 * @Author lipengfei
 * @Date 2023/12/29
 */
public class CommonInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url()
                .newBuilder()
                .scheme(oldRequest.url().scheme())
                .host(oldRequest.url().host())
                .addQueryParameter(ConfigC.INSTANCE.getCLIENT_ID(), ConfigC.INSTANCE.getDEMO_UNSPLASH_CLIENT_ID());
        Request newRequest = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body())
                .url(authorizedUrlBuilder.build())
                .build();
        return chain.proceed(newRequest);
    }
}
