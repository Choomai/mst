package com.baolong.mst

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class TimetableViewModel(mainContext: Context, database: AppDatabase, mainAlarmManager: AlarmManager): ViewModel() {
    val events = mutableStateOf<List<TimetableEvent>>(emptyList())
    private val timetableDao = database.timetableDao()

    private val weakContext = WeakReference(mainContext)
    private fun getContext(): Context? {
        return weakContext.get()
    }
    private val weakAlarmManager = WeakReference(mainAlarmManager)
    private fun getAlarmManager(): AlarmManager? {
        return weakAlarmManager.get()
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun scheduleAlarm(event: TimetableEvent) {
        val context = getContext()
        val alarmManager = getAlarmManager()
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.apply {
            putExtra("event_content", event.content)
            putExtra("event_id", event.hashCode())
            putExtra("event_time", "${event.time}")
            putExtra("event_weekday", event.weekday)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance()
        val targetWeekDay = when (event.weekday) {
            "Thứ 2" -> 2
            "Thứ 3" -> 3
            "Thứ 4" -> 4
            "Thứ 5" -> 5
            "Thứ 6" -> 6
            "Thứ 7" -> 7
            "Chủ Nhật" -> 1
            else -> 2
        }
        val currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.set(Calendar.HOUR_OF_DAY, event.time.hour)
        calendar.set(Calendar.MINUTE, event.time.minute)
        calendar.set(Calendar.SECOND, 0)

//        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            if (currentWeekDay == targetWeekDay) {
                calendar.add(Calendar.DAY_OF_MONTH, 7)
            } else {
                while (calendar.get(Calendar.DAY_OF_WEEK) != targetWeekDay) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        }

        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

//        alarmManager?.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY * 7,
//            pendingIntent
//        )
        event.pendingIntent = pendingIntent
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun rescheduleAlarm(context: Context, intent: Intent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        // Add 7 days to schedule for next week
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


    init {
        viewModelScope.launch(Dispatchers.IO) {
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun insertEvent(event: TimetableEvent) {
        viewModelScope.launch(Dispatchers.IO) @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
            scheduleAlarm(event)
            timetableDao.insertEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun updateEvent(event: TimetableEvent) {
        val alarmManager = getAlarmManager()
        viewModelScope.launch(Dispatchers.IO) @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
            event.pendingIntent?.let { alarmManager?.cancel(it) }
            scheduleAlarm(event)
            timetableDao.updateEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun deleteEvent(event: TimetableEvent) {
        val alarmManager = getAlarmManager()
        viewModelScope.launch(Dispatchers.IO) {
            event.pendingIntent?.let { alarmManager?.cancel(it) }
            timetableDao.deleteEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }
}