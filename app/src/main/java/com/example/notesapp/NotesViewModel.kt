package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ: Flow dari Room diubah menjadi StateFlow untuk UI
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
// CREATE
    fun addNote(title: String, noteContent: String) {
        val cleanTitle = title.trim()
        val cleanNote = noteContent.trim()
        if (cleanTitle.isEmpty() && cleanNote.isEmpty()) return
        viewModelScope.launch {
            dao.insert(Note(title = cleanTitle, note = cleanNote))
        }
    }

    // UPDATE
    fun updateNote(note: Note, newTitle: String, newNoteContent: String) {
        val cleanTitle = newTitle.trim()
        val cleanNote = newNoteContent.trim()
        if (cleanTitle.isEmpty() && cleanNote.isEmpty()) return
        viewModelScope.launch {
            dao.update(note.copy(title = cleanTitle, note = cleanNote))
        }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }
}