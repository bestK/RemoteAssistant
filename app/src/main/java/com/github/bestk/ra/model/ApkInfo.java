package com.github.bestk.ra.model;

import java.util.List;

public class ApkInfo {
    public ApkInfo(String packageName, String appName, List<String> exportedActivityNames) {
        this.packageName = packageName;
        this.appName = appName;
        this.exportedActivityNames = exportedActivityNames;
    }

    public ApkInfo() {
    }

    String packageName;
    String appName;

    List<String> exportedActivityNames;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<String> getExportedActivityNames() {
        return exportedActivityNames;
    }

    public void setExportedActivityNames(List<String> exportedActivityNames) {
        this.exportedActivityNames = exportedActivityNames;
    }
}
