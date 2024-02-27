package com.dafay.demo.lib.base.utils;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Des 反射获取 application
 * @Author lipengfei
 * @Date 2023/6/10 12:04
 */
public class ApplicationUtils {
    private static String TAG = ApplicationUtils.class.getSimpleName();
    public static final String CLASS_FOR_NAME = "android.app.ActivityThread";
    public static final String CURRENT_APPLICATION = "currentApplication";
    public static final String GET_INITIAL_APPLICATION = "getInitialApplication";

    private ApplicationUtils() {
    }

    public static Application getApp() {
        Application application = null;
        Class atClass;
        Method method;
        try {
            atClass = Class.forName(CLASS_FOR_NAME);
            method = atClass.getDeclaredMethod(CURRENT_APPLICATION);
            method.setAccessible(true);
            application = (Application) method.invoke((Object) null);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException |
                 ClassNotFoundException | IllegalAccessException var4) {
            Log.e(TAG, "exception:" + var4);
        }
        if (application != null) {
            return application;
        } else {
            try {
                atClass = Class.forName(CLASS_FOR_NAME);
                method = atClass.getDeclaredMethod(GET_INITIAL_APPLICATION);
                method.setAccessible(true);
                application = (Application) method.invoke((Object) null);
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException |
                     ClassNotFoundException | IllegalAccessException var3) {
                Log.e(TAG, "exception:" + var3);
            }
            return application;
        }
    }
}
