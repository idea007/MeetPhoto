package com.dafay.demo.lib.base.net;


import android.util.Log;
import com.example.demo.lib.net.HttpConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * @Des OkHttp 配置
 * @Author lipengfei
 * @Date 2023/11/27 18:06
 */
public class OkHttpFamily {
    private static final String TAG = OkHttpFamily.class.getSimpleName();
    private static volatile OkHttpClient API;

    private OkHttpFamily() {
    }

    public static OkHttpClient API() {
        if (null == API) {
            synchronized (TAG) {
                if (null == API) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient.Builder builder = new OkHttpClient.Builder();
                    builder.connectTimeout(15L, TimeUnit.SECONDS)
                            .writeTimeout(15L, TimeUnit.SECONDS)
                            .readTimeout(15L, TimeUnit.SECONDS);
                    builder.addInterceptor(logging);
                    for (Interceptor interceptor : HttpConfig.INSTANCE.getConfig().getInterceptors()) {
                        builder.addInterceptor(interceptor);
                        Log.i(TAG, "addInterceptor" + interceptor.getClass().getSimpleName());
                    }
                    API = builder.build();
                }
            }
        }
        return API;
    }
}
