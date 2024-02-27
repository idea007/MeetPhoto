//package com.dafay.demo.lib.base.net;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Interceptor;
//
///**
// * @Des 请求配置统一管理 java 方式
// * @Author lipengfei
// * @Date 2024/2/27
// */
//public class Config {
//    private String baseUrl;
//    private List<Interceptor> interceptors;
//
//    private Config(Builder builder) {
//        this.baseUrl = builder.baseUrl;
//        this.interceptors = builder.interceptors;
//    }
//
//    public static class Builder {
//        private String baseUrl;
//        private List<Interceptor> interceptors = new ArrayList();
//
//        public Builder(String baseUrl) {
//            this.baseUrl = baseUrl;
//        }
//
//        public Builder addInterceptor(Interceptor interceptor) {
//            this.interceptors.add(interceptor);
//            return this;
//        }
//
//        public Config build() {
//            return new Config(this);
//        }
//    }
//}
//
//
