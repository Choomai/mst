package com.baolong.mst

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimetableViewModel(database: AppDatabase): ViewModel() {
    val events = mutableStateOf<List<TimetableEvent>>(emptyList())
    private val timetableDao = database.timetableDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun insertEvent(event: TimetableEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            timetableDao.insertEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun updateEvent(event: TimetableEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            timetableDao.updateEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }

    fun deleteEvent(event: TimetableEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            timetableDao.deleteEvent(event)
            this@TimetableViewModel.events.value = timetableDao.getAllEvents()
        }
    }
}