package com.github.bestk.ra.command

import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.core.getRoot
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.AccessibilityUtil
import java.util.*

class ScrollUpCommand(private val remoteCommand: RemoteCommand,
                      val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        AccessibilityUtil.scrollToTop(
            RemoteAssistantController.service,
            getRoot(false, remoteCommand.pkgName)
        )
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }
}