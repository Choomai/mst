package com.baolong.mst

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    private val channelId = "timetable"
    private val channelName = "Timetable Notification"
    private val importance = NotificationManager.IMPORTANCE_HIGH
    private val settingsKey = "timetable_channel"

    private fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val channelCreated = sharedPreferences.getBoolean(settingsKey, false)

        if (!channelCreated) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Notify user based on their timetable"
            notificationManager.createNotificationChannel(channel)

            // Set the flag to indicate that the channel has been created
            sharedPreferences.edit().putBoolean(settingsKey, true).apply()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context, notificationManager)

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_alarm_24)
            .setContentTitle("Thời gian biểu")
            .setContentText(intent.getStringExtra("event_content"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify((0..1000).random(), notificationBuilder.build())
    }
}