package com.github.bestk.ra.command

import android.content.ComponentName
import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import java.util.*


class IntentCommand(
    private val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        if (remoteCommand.pkgName.isNullOrBlank()) {
            return null
        }
        if (remoteCommand.activityName.isNullOrBlank()) {
            val intent =
                Utils.getApp().packageManager.getLaunchIntentForPackage(remoteCommand.pkgName)
            if (intent != null) {
                intent.apply {
                    this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    Utils.getApp().startActivity(this)
                }
            } else {
                ToastUtils.showLong("包名 ${remoteCommand.pkgName} 不支持跳转,请尝试指定 activityName 参数")
            }
        } else {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component =
                ComponentName(remoteCommand.pkgName, remoteCommand.activityName)
            Utils.getApp().startActivity(intent)
        }

        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}