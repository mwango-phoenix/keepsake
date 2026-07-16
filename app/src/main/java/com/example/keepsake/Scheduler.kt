package com.example.keepsake

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import androidx.core.net.toUri

/** How often the widget advances to the next photo. */
const val ROTATION_INTERVAL_MS = 10_000L // 10 seconds

private const val ROTATION_REQUEST_CODE = 1001

/**
 * Schedules the next rotation tick.
 */
fun scheduleNextRotation(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = rotationPendingIntent(context)
    val triggerAt = SystemClock.elapsedRealtime() + ROTATION_INTERVAL_MS

    if (hasExactAlarmPermission(context)) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAt,
            pendingIntent
        )
    } else {
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
    }
}

fun cancelRotation(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(rotationPendingIntent(context))
}

private fun rotationPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, PhotoRotationReceiver::class.java)
    return PendingIntent.getBroadcast(
        context,
        ROTATION_REQUEST_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

/** Exact alarms need this special permission on API 31+ (Android 12+). */
fun hasExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true // not required below Android 12
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return alarmManager.canScheduleExactAlarms()
}

fun requestExactAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
        data = "package:${context.packageName}".toUri()
    }
    context.startActivity(intent)
}