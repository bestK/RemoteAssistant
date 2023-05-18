package com.github.bestk.ra.command

import com.github.bestk.ra.core.getNodeInfo
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.AccessibilityUtil
import java.util.*

class ClickCommand(
    private val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        AccessibilityUtil.findTextAndClick(
            getNodeInfo(remoteCommand.pkgName),
            remoteCommand.text
        )
        return null
    }

    override fun onSuccess(result: Properties?) {
        val value = result ?: Properties()
        value["adidas"] = "as-asd"
        super.onSuccess(value)
    }
}