package com.dafay.demo.lib.base.net;

import androidx.annotation.NonNull;

import com.example.demo.lib.net.HttpConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Des Retrofit 配置 [单例]
 * @Author lipengfei
 * @Date 2023/11/27 18:06
 */
public class RetrofitFamily {

    private static Retrofit retrofit;
    // 使用 map 缓存已存在的
    private static final Map<Class<?>, Object> SERVICE_MAP = new ConcurrentHashMap<>();

    private RetrofitFamily() {
    }

    public static synchronized Retrofit getInstance() {
        if (null == retrofit) {
            retrofit = new Retrofit.Builder()
                    .client(OkHttpFamily.API())
                    .baseUrl(HttpConfig.INSTANCE.getConfig().getBaseUrl())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 创建 Retrofit service。
     */
    public static <T> T createService(@NonNull Class<T> service) {
        T serviceImpl = (T) SERVICE_MAP.get(service);
        if (null == serviceImpl) {
            serviceImpl = createService(getInstance(), service);
            SERVICE_MAP.put(service, serviceImpl);
        }
        return serviceImpl;
    }

    private static <T> T createService(Retrofit retrofit, Class<T> service) {
        return retrofit.create(service);
    }

    /**
     * 切换环境后重置
     */
    static synchronized void reset() {
        retrofit = null;
        SERVICE_MAP.clear();
    }
}
