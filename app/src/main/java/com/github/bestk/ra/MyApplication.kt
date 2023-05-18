package com.github.bestk.ra

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.evernote.android.job.JobManager
import com.github.bestk.ra.config.GlobalException
import com.github.bestk.ra.core.scheduled.ScheduledJobCreator
import com.hjq.toast.ToastUtils


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        //初始化 Toast 框架
        ToastUtils.init(this)
        //初始化前台服务
//        PlayNotifyManager.show()
        //设置全局异常捕获重启
        Thread.setDefaultUncaughtExceptionHandler(GlobalException.getInstance())

        // 任务
        JobManager.create(this).addJobCreator(ScheduledJobCreator())

    }



}