package com.github.bestk.ra.command

import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.core.getNodeInfo
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.AccessibilityUtil
import java.util.*

class ScrollDownCommand(
    private val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        AccessibilityUtil.scrollToBottom(
            RemoteAssistantController.service,
            getNodeInfo(remoteCommand.pkgName)
        )
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}