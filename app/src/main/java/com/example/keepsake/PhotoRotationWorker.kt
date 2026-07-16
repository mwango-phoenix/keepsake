package com.example.keepsake

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Advances the widget to the next photo.
 */
class PhotoRotationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        advanceAllPhotoWidgets(applicationContext)
        return Result.success()
    }
}

private const val ROTATION_WORK_NAME = "photo_rotation_work"
fun schedulePhotoRotation(context: Context) {
    val request = PeriodicWorkRequestBuilder<PhotoRotationWorker>(10, TimeUnit.SECONDS).build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        ROTATION_WORK_NAME,
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}