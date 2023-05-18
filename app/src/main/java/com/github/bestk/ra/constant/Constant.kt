package com.github.bestk.ra.constant

import com.blankj.utilcode.util.SPUtils

object Constant {

    const val LONG_INTERVAL = 5000L
    const val CHANGE_PAGE_INTERVAL = 1000L
    const val POP_WINDOW_INTERVAL = 500L
    private const val DEFAULT_HOST = "ws://192.168.0.165:3000"



    var PACKAGE_NAME: String
        get() = SPUtils.getInstance().getString("packageName", "")
        set(value) {
            SPUtils.getInstance().put("packageName", value)
        }

    var WS_STATUS: String
        get() = SPUtils.getInstance().getString("wsStatus", "")
        set(value) {
            SPUtils.getInstance().put("wsStatus", value)
        }

    var HOST: String
        get() = SPUtils.getInstance().getString("host", DEFAULT_HOST)
        set(value) {
            SPUtils.getInstance().put("host", value)
        }

    var ROBOT_ID: String
        get() = SPUtils.getInstance().getString("robotId")
        set(value) {
            SPUtils.getInstance().put("robotId", value)
        }
}