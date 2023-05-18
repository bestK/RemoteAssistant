package com.github.bestk.ra.utils

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils
import com.github.bestk.ra.constant.Constant
import com.github.bestk.ra.core.RemoteAssistantController
import java.io.File

/**
 * 文件分享工具类
 */
object ShareUtil {

    /**
     * Share Text
     */
    const val TEXT = "text/plain"

    /**
     * Share Image
     */
    const val IMAGE = "image/*"

    /**
     * Share Audio
     */
    const val AUDIO = "audio/*"

    /**
     * Share Video
     */
    const val VIDEO = "video/*"

    /**
     * Share File
     */
    const val File = "*/*"

    /**
     * 文件分享 需要先授权显示悬浮窗
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun share(type: String, path: String): Boolean {
        return share(type, File(path))
    }

    /**
     * 文件分享 需要先授权显示悬浮窗
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun share(type: String, file: File): Boolean {
        val root = RemoteAssistantController.service.rootInActiveWindow
        if (root.packageName != Constant.PACKAGE_NAME) {
            LogUtils.e("文件分享失败 当前应用不在前台")
            return false
        }
        val app = Utils.getApp()
        if (!Settings.canDrawOverlays(app)) {
            LogUtils.e("文件分享失败 没有悬浮窗权限~")
            return false
        }
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.type = type
            val fileURI = FileProvider.getUriForFile(app, app.packageName + ".fileprovider", file)
            putExtra(Intent.EXTRA_STREAM, fileURI)
        }
        intent.setPackage(Constant.PACKAGE_NAME)
        app.startActivity(Intent.createChooser(intent, "WorkTool文件分享").apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        LogUtils.e("分享了 $type ${file.absolutePath}")
        return true
    }

}