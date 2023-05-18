package com.github.bestk.ra.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.Utils
import com.github.bestk.ra.constant.Constant
import com.github.bestk.ra.constant.Views
import com.github.bestk.ra.utils.AccessibilityUtil
import com.github.bestk.ra.utils.FloatWindowHelper
import com.hjq.toast.ToastUtils


var requestCode = 1000000
fun fastStartActivity(
    context: Context,
    clazz: Class<*>,
    flags: Int = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP,
    i: Intent? = null
) {
    val intent = i ?: Intent(context, clazz)
    intent.flags = flags
    val pendingIntent = PendingIntent.getActivity(context, requestCode++, intent, 0)
    try {
        pendingIntent.send()
    } catch (e: PendingIntent.CanceledException) {
        e.printStackTrace()
        context.startActivity(intent)
    }
}

/**
 * 进入首页-消息页
 */
fun goHome() {

}

/**
 * 进入首页tab
 * 1.检查是否有底部tab
 * 2.回退到首页
 * @param title 消息/文档/通讯录/工作台/我
 * 可能因为管理员排版首页Tab而导致找不到匹配title
 */
fun goHomeTab(title: String): Boolean {
    var find = false
    while (!find) {
        val list = AccessibilityUtil.findAllOnceByText(getRoot(), title, exact = true)
        for (item in list) {
            val childCount = item.parent?.parent?.parent?.childCount
            if (childCount == 4 || childCount == 5) {
                //处理侧边栏抽屉打开
                if (title == "消息") {
                    val rect = Rect()
                    item.getBoundsInScreen(rect)
                    if (rect.left > ScreenUtils.getScreenWidth() / 2) {
                        return goHomeTab("工作台") && goHomeTab("消息")
                    }
                }
                if (!item.isSelected) {
                    AccessibilityUtil.performClick(item)
                    sleep(300)
                }
                find = true
            }
        }
        if (!find) {
            if (isAtHome()) {
                return false
            } else {
                backPress()
            }
        }
    }
    LogUtils.v("进入首页-${title}页")
    return find
}

/**
 * 当前是否在首页
 */
fun isAtHome(): Boolean {
    val list = AccessibilityUtil.findAllOnceByText(getRoot(), "消息", exact = true)
    val item = list.firstOrNull {
        val childCount = it.parent?.parent?.parent?.childCount
        (childCount == 4 || childCount == 5)
    } ?: return false
    if (!item.isSelected) {
        AccessibilityUtil.performClick(item)
        sleep(300)
    }
    return true
}

/**
 * 获取企业微信窗口
 */
fun getRoot(): AccessibilityNodeInfo {
    return getRoot(false)
}

/**
 * 获取前台窗口
 * @param ignoreCheck false
 */
fun getRoot(ignoreCheck: Boolean, packageName: String? = null): AccessibilityNodeInfo {
    while (true) {
        val tempRoot = RemoteAssistantController.service.rootInActiveWindow
        val root = RemoteAssistantController.service.rootInActiveWindow

        if (tempRoot != root) {
            LogUtils.e("tempRoot != root")
        } else if (root != null) {
            if (packageName.isNullOrBlank() || root.packageName == packageName) {
                ToastUtils.showLong("root.packageName:${root.packageName}")
                return root
            } else {
                LogUtils.e("当前不在命令要求 package: ${root.packageName}")
                RemoteAssistantController.service.currentPackage =
                    root.packageName?.toString() ?: ""
                if (System.currentTimeMillis() % 30 == 0L) {
                    if (!root.packageName.equals(packageName)) {
                        if (!FloatWindowHelper.isPause) {
                            ToastUtils.show("当前包名: ${root.packageName}\n尝试跳转到: $packageName")
                            Utils.getApp().packageManager.getLaunchIntentForPackage(packageName)
                                ?.apply {
                                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    Utils.getApp().startActivity(this)
                                }
                        }
                    }
                }
                if (ignoreCheck) {
                    return root
                }
            }
        }
        sleep(Constant.CHANGE_PAGE_INTERVAL)
    }
}


fun getNodeInfo(packageName: String?): AccessibilityNodeInfo {
    while (true) {
        val service = RemoteAssistantController.service
        val tempRoot = service.rootInActiveWindow
        val root = service.rootInActiveWindow
        if (tempRoot != root) {
            LogUtils.e("tempRoot != root")
        } else if (root != null) {
            val currentPackage = root.packageName
            if (packageName == null || currentPackage == packageName) {
                return root
            } else {
                service.currentPackage = root.packageName?.toString() ?: ""
                if (!root.packageName.contains(packageName)) {
                    if (!FloatWindowHelper.isPause) {
                        LogUtils.e("当前包名: $currentPackage 尝试跳转到: $packageName")
                        try {
                            Utils.getApp().packageManager.getLaunchIntentForPackage(packageName)
                                ?.apply {
                                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    Utils.getApp().startActivity(this)
                                }
                        } catch (e: Exception) {
                            ToastUtils.show("尝试跳转到: $packageName 失败：$e")
                        }
                    }
                }
            }
        }

        sleep(Constant.CHANGE_PAGE_INTERVAL)
    }
}

/**
 * 后退
 */
fun backPress() {
    val clazz = RemoteAssistantController.service.currentClass
    val textView = AccessibilityUtil.findOnceByClazz(getRoot(), Views.TextView)
    if (textView != null && textView.text.isNullOrBlank() && AccessibilityUtil.performClick(
            textView, retry = false
        )
    ) {
        LogUtils.v("找到回退按钮")
    } else {
        val ivButton = AccessibilityUtil.findOnceByClazz(getRoot(), Views.ImageView)
        if (ivButton != null && ivButton.isClickable && AccessibilityUtil.findFrontNode(ivButton) == null) {
            LogUtils.d("未找到回退按钮 点击第一个IV按钮")
            AccessibilityUtil.performClick(ivButton, retry = false)
        } else {
            LogUtils.d("未找到回退按钮 点击第一个BT按钮")
            val button = AccessibilityUtil.findOnceByClazz(getRoot(), Views.Button)
            if (button != null && button.childCount > 0) {
                AccessibilityUtil.performClick(button.getChild(0), retry = false)
            } else if (button != null) {
                AccessibilityUtil.performClick(button, retry = false)
            } else {
                LogUtils.d("未找到BT按钮")
                val confirm = AccessibilityUtil.findOnceByText(
                    getRoot(), "确定", "我知道了", "暂不进入", "不用了", "取消", "暂不", "关闭", "留在企业微信", exact = true
                )
                if (confirm != null) {
                    LogUtils.d("尝试点击确定/我知道了/暂不进入")
                    AccessibilityUtil.performClick(confirm)
                } else {
                    val stayButton =
                        AccessibilityUtil.findOnceByText(getRoot(true), "关闭应用", "等待", exact = true)
                    if (stayButton != null) {
                        LogUtils.d("疑似ANR 尝试点击等待")
                        AccessibilityUtil.performClick(stayButton)
                    } else {
                        LogUtils.d("未找到对话框 点击bar中心")
                        AccessibilityUtil.performXYClick(
                            RemoteAssistantController.service,
                            ScreenUtils.getScreenWidth() / 2F,
                            BarUtils.getStatusBarHeight() * 2F
                        )
                        sleep(Constant.CHANGE_PAGE_INTERVAL * 2)
                        if (RemoteAssistantController.service.currentClass == clazz) {
                            val firstEmptyTextView =
                                AccessibilityUtil.findAllByClazz(getRoot(), Views.TextView)
                                    .firstOrNull { it.text.isNullOrEmpty() }
                            if (firstEmptyTextView != null && firstEmptyTextView.isClickable) {
                                AccessibilityUtil.performClick(firstEmptyTextView)
                            }
                            sleep(Constant.CHANGE_PAGE_INTERVAL)
                            if (RemoteAssistantController.service.currentClass == clazz && RemoteAssistantController.service.currentPackage == Constant.PACKAGE_NAME) {
                                AccessibilityUtil.globalGoBack(RemoteAssistantController.service)
                            }
                        }
                    }
                }
            }
        }
    }
    sleep(Constant.POP_WINDOW_INTERVAL)
}


/**
 * 简单封装 sleep
 */
fun sleep(time: Long) {
    try {
        Thread.sleep(time)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}