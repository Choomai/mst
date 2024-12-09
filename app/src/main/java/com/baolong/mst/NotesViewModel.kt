package com.baolong.mst

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(database: AppDatabase): ViewModel() {
    val notes = mutableStateOf<List<Note>>(emptyList())
    private val noteDao = database.noteDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            this@NotesViewModel.notes.value = noteDao.getAllNotes()
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.insertNote(note)
            this@NotesViewModel.notes.value = noteDao.getAllNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(note)
            this@NotesViewModel.notes.value = noteDao.getAllNotes()
        }
    }
}