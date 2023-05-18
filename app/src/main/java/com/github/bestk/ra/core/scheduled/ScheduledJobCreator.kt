package com.github.bestk.ra.core.scheduled

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class ScheduledJobCreator : JobCreator {

    override fun create(tag: String): Job {
        return when(tag){
            ScheduledCommandJob.TAG -> ScheduledCommandJob()
            else -> ScheduledCommandJob()
        }
    }

}