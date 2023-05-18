package com.github.bestk.ra.command

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.blankj.utilcode.util.*
import com.github.bestk.ra.core.RemoteAssistantController
import com.github.bestk.ra.core.scheduled.ScheduledJobService
import com.github.bestk.ra.core.sleep
import com.github.bestk.ra.model.CommandResult
import com.github.bestk.ra.model.RemoteCommand
import com.github.bestk.ra.utils.PeriodicUtils
import java.util.*


class ScheduleCommand(
    val remoteCommand: RemoteCommand,
    val result: MutableList<CommandResult>? = null
) : AbstractCommand(remoteCommand, result) {
    override fun doInBackground(): Properties? {
        if (remoteCommand.cronExpression.isNullOrBlank()) {
            showError("cronExpression 不能为空")
            return null
        }

        if (PeriodicUtils.gt15Minutes(remoteCommand.cronExpression)) {
            jobScheduler()
        } else {
            showError("${remoteCommand.cronExpression} 时间间隔小于15分钟使用自定义调度执行")
            SPUtils.getInstance().put("jobId${remoteCommand.text}Status", true)
            whileDelayScheduler()
        }

        return null
    }

    override fun onSuccess(result: Properties?) {
        super.onSuccess(result)
    }

    private fun jobScheduler() {
        val jobScheduler =
            Utils.getApp().getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val builder = JobInfo.Builder(
            remoteCommand.text.toInt(),
            ComponentName(Utils.getApp(), ScheduledJobService::class.java)
        )
        // 设置作业执行的间隔时间
        builder.setPeriodic(PeriodicUtils.getDelay(remoteCommand.cronExpression))
        val extras = PersistableBundle()
        extras.putString("jobParams", GsonUtils.toJson(remoteCommand))
        builder.setExtras(extras)
        // 开始调度作业
        jobScheduler.schedule(builder.build())
    }

    /**
     * 使用 while 循环加延时实现定时任务
     */
    private fun whileDelayScheduler() {
        val delay = PeriodicUtils.getDelay(remoteCommand.cronExpression)
        val doJob = SPUtils.getInstance().getBoolean("jobId${remoteCommand.text}Status")
        while (doJob) {
            for (remoteCommand in remoteCommand.subCommands) {
                RemoteAssistantController.service.handleRemoteCommand(remoteCommand)
            }
            sleep(delay)
        }
    }

    private fun showError(message: String) {
        ToastUtils.showLong(message)
        LogUtils.e(message)
    }
}