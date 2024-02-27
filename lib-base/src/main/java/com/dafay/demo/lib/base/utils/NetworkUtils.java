package com.dafay.demo.lib.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * 网络连接Utils
 */
public class NetworkUtils {


    public enum NetworkConnectState {
        WIFI,       //wifi
        MONET,      //移动数据
        NO_NETWORK  //无网络或未知情况
    }


    public static boolean isConnected(Context context){
        NetworkConnectState currentConnectState=getCurrentConnectState(context);
        if(currentConnectState== NetworkConnectState.WIFI||currentConnectState== NetworkConnectState.MONET){
            return true;
        }else {
            return false;
        }
    }


    /**
     * @param context
     * @return 如果判断有wifi, 直接返回
     */
    public static NetworkConnectState getCurrentConnectState(Context context) {

        NetworkConnectState currentConnectState = NetworkConnectState.NO_NETWORK;

        //检测API是不是小于21，因为到了API21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiNetworkInfo.isConnected()) {
                currentConnectState = NetworkConnectState.WIFI;
            } else if (dataNetworkInfo.isConnected()) {
                currentConnectState = NetworkConnectState.MONET;
            } else {
                currentConnectState = NetworkConnectState.NO_NETWORK;
            }

        } else {

            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);

                if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    currentConnectState = NetworkConnectState.WIFI;
                    return currentConnectState;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    currentConnectState = NetworkConnectState.WIFI;
                    return currentConnectState;
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    currentConnectState = NetworkConnectState.MONET;
                } else {
                    currentConnectState = NetworkConnectState.NO_NETWORK;
                }
            }
        }

        return currentConnectState;
    }
}
