package com.example.mob_dev_portfolio.utils

import FunFactWorker
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // on app start queue notifications
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {

            val workRequest = OneTimeWorkRequestBuilder<FunFactWorker>()
                .setInitialDelay(15, TimeUnit.SECONDS)  // Short intervals for testing purposes
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
