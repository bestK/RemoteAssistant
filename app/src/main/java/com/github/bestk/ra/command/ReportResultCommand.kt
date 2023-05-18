package com.github.bestk.ra.command

import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import java.util.*

class ReportResultCommand(
    remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        LogUtils.d("ReportResultCommand ${GsonUtils.toJson(result)}")
        //TODO report to server
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}