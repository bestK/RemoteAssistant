package com.github.bestk.ra.command

import com.blankj.utilcode.util.ThreadUtils
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import java.util.*

abstract class AbstractCommand(
    private val remoteCommand: RemoteCommand?,
    private val commandResult: MutableList<CommandResult>?
) : ThreadUtils.SimpleTask<Properties>() {

    override fun doInBackground(): Properties? {
        return null
    }

    override fun onSuccess(result: Properties?) {
        commandResult?.add(CommandResult(remoteCommand, result))
    }

    fun execute() {
        ThreadUtils.executeByIo(this)
    }
}