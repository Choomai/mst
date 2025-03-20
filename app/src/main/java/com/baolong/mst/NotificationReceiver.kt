package com.baolong.mst

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import android.icu.util.Calendar
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import androidx.core.content.edit

class NotificationReceiver : BroadcastReceiver() {
    private val channelId = "timetable"
    private val channelName = "Timetable Notification"
    private val importance = NotificationManager.IMPORTANCE_HIGH
    private val settingsKey = "timetable_channel"

    private fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val channelCreated = sharedPreferences.getBoolean(settingsKey, false)

        if (!channelCreated) {
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Notify user based on their timetable"
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            notificationManager.createNotificationChannel(channel)
            // Set the flag to indicate that the channel has been created
            sharedPreferences.edit { putBoolean(settingsKey, true) }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        createNotificationChannel(context, notificationManager)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle("Thời gian biểu")
            .setContentText(intent.getStringExtra("event_content"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
            notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        }

        notificationManager.notify(intent.getIntExtra("event_id", 0), notificationBuilder.build())

        // Reschedule for next week
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_MONTH, 7)
        calendar.set(Calendar.HOUR_OF_DAY, intent.getStringExtra("event_time")?.split(":")?.get(0)?.toInt() ?: 0)
        calendar.set(Calendar.MINUTE, intent.getStringExtra("event_time")?.split(":")?.get(1)?.toInt() ?: 0)
        calendar.set(Calendar.SECOND, 0)

        val newIntent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("event_content", intent.getStringExtra("event_content"))
            putExtra("event_id", intent.getIntExtra("event_id", 0))
            putExtra("event_time", intent.getStringExtra("event_time"))
            putExtra("event_weekday", intent.getStringExtra("event_weekday"))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            intent.getIntExtra("event_id", 0),
            newIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM)
            == PackageManager.PERMISSION_GRANTED) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}