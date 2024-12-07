package com.baolong.mst

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable_events")
data class TimetableEvent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val weekday: Int,
    val time: Long
)