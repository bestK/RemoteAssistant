package com.github.bestk.ra.core.scheduled

import android.os.Bundle
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.cron4j.Predictor
import com.github.bestk.ra.utils.cron4j.SchedulingPattern
import java.util.*


class ScheduledCommandJob : Job() {

    companion object {
        const val TAG = "scheduled_command_job"
    }

    private var nextExecution: Date? = null

    override fun onRunJob(params: Params): Result {
        val scheduledCommandJson = params.transientExtras.getString("scheduledCommand")
        val scheduledCommand = GsonUtils.fromJson(
            scheduledCommandJson,
            RemoteCommand::class.java
        )

        for (remoteCommand in scheduledCommand.subCommands) {
            RemoteAssistantController.service.handleRemoteCommand(remoteCommand)
        }

        return Result.SUCCESS
    }


    fun schedule(remoteCommand: RemoteCommand) {
        if (remoteCommand.cronExpression.isNullOrBlank() || remoteCommand.subCommands.isEmpty()) {
            ToastUtils.showLong("定时任务配置错误！")
            return
        }
        initNext(remoteCommand.cronExpression)
        LogUtils.d("nextExecution:${TimeUtils.date2String(nextExecution)} ")
        if (nextExecution != null) {
            JobRequest.Builder(TAG)
                .setPeriodic(nextExecution!!.time)
                .setTransientExtras(createTransientExtras(remoteCommand))
                .setRequirementsEnforced(true)
                .build()
                .schedule()
        }

    }

    private fun createTransientExtras(remoteCommand: RemoteCommand): Bundle {
        val extras = Bundle()
        extras.putString("scheduledCommand", GsonUtils.toJson(remoteCommand))
        return extras
    }

    private fun initNext(cronExpression: String) {
        val pattern = SchedulingPattern(cronExpression)
        val predictor = Predictor(pattern)
        nextExecution = predictor.nextMatchingDate()
    }


}