package com.github.bestk.ra.command

import com.blankj.utilcode.util.ToastUtils
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import java.util.*

class ToastCommand(
    private val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        ToastUtils.showLong(remoteCommand.text)
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}