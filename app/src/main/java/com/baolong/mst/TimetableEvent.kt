package com.baolong.mst

import android.app.PendingIntent
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "timetable_events")
data class TimetableEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val weekday: String,
    val time: LocalTime,
) {
    @Ignore
    var pendingIntent: PendingIntent? = null
}