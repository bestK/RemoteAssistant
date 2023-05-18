package com.github.bestk.ra.utils

import com.github.bestk.ra.utils.cron4j.Predictor
import com.github.bestk.ra.utils.cron4j.SchedulingPattern
import java.util.*
import java.util.concurrent.TimeUnit

class PeriodicUtils {

    companion object {

        /**
         * 根据 cron 获取延时时间
         */
        @JvmStatic
        fun getDelay(cronExpression: String): Long {
            return getMatchIngDate(cronExpression).time - System.currentTimeMillis()
        }

        /**
         * 根据 cron 是否大于15分钟
         */
        @JvmStatic
        fun gt15Minutes(cronExpression: String): Boolean {
            val matchIngDate = getMatchIngDate(cronExpression)
            return getMinuteDiff(Date(), matchIngDate) > 15
        }

        /**
         * 根据 cron 是否大于15分钟
         */
        @JvmStatic
        fun getMatchIngDate(cronExpression: String): Date {
            val pattern = SchedulingPattern(cronExpression)
            val predictor = Predictor(pattern)
            return predictor.nextMatchingDate()
        }

        @JvmStatic
        fun getMinuteDiff(date1: Date, date2: Date): Long {
            val diffInMillis = date2.time - date1.time
            return TimeUnit.MINUTES.convert(diffInMillis, TimeUnit.MILLISECONDS)
        }

    }

}