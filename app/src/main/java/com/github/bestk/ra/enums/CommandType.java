package com.github.bestk.ra.enums;

/**
 * 命令类型枚举类
 */
public enum CommandType {
    /**
     * 打开应用包
     */
    INTENT,

    /**
     * 点击
     */
    CLICK,

    /**
     * 返回
     */
    BACK,

    /**
     * 向上滚动
     */
    SCROLL_UP,

    /**
     * 向下滚动
     */
    SCROLL_DOWN,

    /**
     * 回到主屏幕
     */
    HOME,

    /**
     * 向左滑动
     */
    SWIPE_LEFT,

    /**
     * 向右滑动
     */
    SWIPE_RIGHT,

    /**
     * 获取已安装的应用包
     */
    GET_INSTALL_PACKAGE,

    /**
     * 睡眠
     */
    SLEEP,

    /**
     * 提示
     */
    TOAST,

    /**
     * 序列
     */
    SEQUENCE,

    /**
     * 定时任务
     */
    SCHEDULE,

    /**
     * 上报执行结果
     */
    REPORT_RESULT
}
