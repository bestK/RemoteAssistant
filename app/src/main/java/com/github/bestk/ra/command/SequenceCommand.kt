package com.github.bestk.ra.command

import com.blankj.utilcode.util.LogUtils
import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.core.sleep
import com.github.bestk.ra.enums.CommandType
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.google.gson.Gson
import java.util.*


class SequenceCommand(
    private val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {

    override fun doInBackground(): Properties? {
        executeSequence(remoteCommand.subCommands)
        super.doInBackground()
        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }


    private fun executeSequence(
        subCommands: List<RemoteCommand>
    ) {
        for (subCommand in subCommands) {
            LogUtils.i("执行命令序列：${Gson().toJson(subCommand)}")
            if (subCommand.commandType == CommandType.SLEEP) {
                sleep(subCommand.text.toLong())
            } else {
                RemoteAssistantController.service.handleRemoteCommand(
                    subCommand,
                    result
                )
            }
        }
    }
}