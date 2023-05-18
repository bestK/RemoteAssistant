package com.github.bestk.ra.core.scheduled

import android.app.job.JobParameters
import android.app.job.JobService
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.github.bestk.ra.model.RemoteCommand

class ScheduledJobService : JobService() {

    private val TAG = "ScheduledJobService"

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        LogUtils.d(TAG, "onStartJob: called", jobParameters)
        val jobParams = jobParameters?.extras?.getString("jobParams")
        val remoteCommand = GsonUtils.fromJson<RemoteCommand>(jobParams, RemoteCommand::class.java)
        ScheduledCommandJob().schedule(remoteCommand)
        return true
    }

    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        LogUtils.d(TAG, "onStopJob: called");
        return false
    }

}