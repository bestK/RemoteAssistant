package com.github.bestk.ra.command

import com.blankj.utilcode.util.GsonUtils
import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.CommonUtils
import java.util.*

class GetInstalledPkgCommand(
    val remoteCommand: RemoteCommand?= null,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand,result) {
    override fun doInBackground(): Properties? {
        val installedAppPackageNames = CommonUtils.getInstalledAppPackageNames()
        RemoteAssistantController.service.webSocketHelper.send(
            GsonUtils.toJson(
                installedAppPackageNames
            )
        )
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}