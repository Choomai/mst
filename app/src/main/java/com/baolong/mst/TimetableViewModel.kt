package com.baolong.mst

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.compose.runtime.mutableStateOf
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

    private fun scheduleAlarm(event: TimetableEvent) {
        val context = getContext()
        val alarmManager = getAlarmManager()
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("event_content", event.content)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = Calendar.getInstance()
        val weekday = when (event.weekday) {
            "Thứ 2" -> 2
            "Thứ 3" -> 3
            "Thứ 4" -> 4
            "Thứ 5" -> 5
            "Thứ 6" -> 6
            "Thứ 7" -> 7
            "Chủ Nhật" -> 1
            else -> 2
        }
        calendar.set(Calendar.DAY_OF_WEEK, weekday)
        calendar.set(Calendar.HOUR_OF_DAY, event.time.hour)
        calendar.set(Calendar.MINUTE, event.time.minute)

//        alarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
        event.pendingIntent = pendingIntent
    }


    init {
        viewModelScope.launch(Dispatchers.IO) {
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun insertEvent(event: TimetableEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleAlarm(event)
            timetableDao.insertEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun updateEvent(event: TimetableEvent) {
        val alarmManager = getAlarmManager()
        viewModelScope.launch(Dispatchers.IO) {
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