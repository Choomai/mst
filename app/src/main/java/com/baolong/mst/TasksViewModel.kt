package com.baolong.mst

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TasksViewModel(database: AppDatabase): ViewModel() {
    val tasks = mutableStateOf<List<Task>>(emptyList())
    private val taskDao = database.taskDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            this@TasksViewModel.tasks.value = taskDao.getAllTasks()
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
            this@TasksViewModel.tasks.value = taskDao.getAllTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task)
            this@TasksViewModel.tasks.value = taskDao.getAllTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task)
            this@TasksViewModel.tasks.value = taskDao.getAllTasks()
        }
    }
}