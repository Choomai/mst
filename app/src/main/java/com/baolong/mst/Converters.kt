package com.baolong.mst

import androidx.room.TypeConverter
import java.time.LocalTime

class Converters {
    @TypeConverter
    fun localTimeToLong(time: LocalTime?): Long? {
        return time?.let { it.toSecondOfDay().toLong() * 1000 }
    }

    @TypeConverter
    fun longToLocalTime(timeInMillis: Long?): LocalTime? {
        return timeInMillis?.let { it / 1000 }?.let { LocalTime.ofSecondOfDay(it) }
    }
}