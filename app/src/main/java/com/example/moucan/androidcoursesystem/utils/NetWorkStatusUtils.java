package com.example.moucan.androidcoursesystem.utils;

import com.example.moucan.androidcoursesystem.base.Application;

/**
 * @packagename moucan.androidcoursesystem.utils
 * desc 判断当前手机网络类型
 */

public class NetWorkStatusUtils {
    public static void internal() {
        int networkType = NetWorkUtils.getNetworkType(Application.context);
        String networkTypeName = NetWorkUtils.getNetworkTypeName(Application.context);
        LogUtils.d("-----网络名字-----", networkTypeName);
        LogUtils.d("----网络类型-----", networkType + "");
        if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_WIFI)) {
            ToastUtils.showToast("你目前处于wifi网络");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
            ToastUtils.showToast("你目前处于断网状态");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_3G)) {
            ToastUtils.showToast("你目前处于3G状态");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_2G)) {
            ToastUtils.showToast("你目前处于2G网络");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_WAP)) {
            ToastUtils.showToast("你目前处于企业网");
        } else if (networkTypeName.equals(NetWorkUtils.NETWORK_TYPE_UNKNOWN)) {
            ToastUtils.showToast("你目前网络类型不知道");
        }
    }
}
