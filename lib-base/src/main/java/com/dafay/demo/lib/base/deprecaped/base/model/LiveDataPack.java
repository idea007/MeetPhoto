package com.dafay.demo.lib.base.deprecaped.base.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LiveDataPack
 * @Des 数据包
 * @Author lipengfei
 * @Date 2023/5/20 16:00
 */
public class LiveDataPack<T> {
    public RequestState state;
    public T data;
    public Throwable throwable;
    public String errorTip;
    private Map<String, Object> map;

    public LiveDataPack(RequestState state) {
        this.state = state;
    }

    public LiveDataPack(RequestState state, T data) {
        this.state = state;
        this.data = data;
    }

    public LiveDataPack(RequestState state, Throwable throwable) {
        this.state = state;
        this.throwable = throwable;
    }

    public LiveDataPack(RequestState state, Throwable throwable, String errorTip) {
        this.state = state;
        this.throwable = throwable;
        this.errorTip = errorTip;
    }

    public <K> void put(String key, K o) {
        if (o == null) {
            return;
        }
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, o);
    }

    public <K> K get(String key) {
        if (map == null || !map.containsKey(key)) {
            return null;
        }
        return (K) map.get(key);
    }
}
