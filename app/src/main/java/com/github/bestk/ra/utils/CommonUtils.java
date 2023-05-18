package com.github.bestk.ra.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.blankj.utilcode.util.Utils;
import com.github.bestk.ra.model.ApkInfo;

import java.util.ArrayList;
import java.util.List;

public class CommonUtils {

    /**
     * @return 已安装的包名
     */
    public static List<ApkInfo> getInstalledAppPackageNames() {
        PackageManager packageManager = Utils.getApp().getPackageManager();
        @SuppressLint("QueryPermissionsNeeded")
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);

        List<ApkInfo> apkInfo = new ArrayList<>();

        for (ApplicationInfo applicationInfo : installedApplications) {
            String appName = packageManager.getApplicationLabel(applicationInfo).toString();
            apkInfo.add(new ApkInfo(applicationInfo.packageName, appName, getExportedActivityNames(Utils.getApp(), applicationInfo.packageName)));
        }
        return apkInfo;
    }


    public static List<String> getExportedActivityNames(Context context, String packageName) {
        List<String> activityNames = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        try {
            // 获取指定包名的应用程序信息
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            // 遍历所有的 Activity，并检查其 exported 属性的值
            if (packageInfo.activities != null) {
                for (ActivityInfo activityInfo : packageInfo.activities) {
                    if (activityInfo.exported) {
                        // 添加 Activity 类名到列表中
                        activityNames.add(activityInfo.name);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            // 包名不存在
        }
        return activityNames;
    }


}
