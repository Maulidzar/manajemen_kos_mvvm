package com.example.kosmvvm

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.kosmvvm.worker.NotificationWorker
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleDailyNotificationWorker()
    }

    private fun scheduleDailyNotificationWorker() {
        val reminderRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyPaymentReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}