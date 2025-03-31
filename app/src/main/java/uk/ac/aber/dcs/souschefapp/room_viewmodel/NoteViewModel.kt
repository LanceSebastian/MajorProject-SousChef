package uk.ac.aber.dcs.souschefapp.room_viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.models.Note

/*          Handles Recipe Note Manipulation        */
class NoteViewModel(
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)


    fun insertNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteNote(note)
        }
    }

    fun deleteNote(noteId: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteNote(noteId)
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateNote(note)
        }
    }

    fun getNotesFromRecipe(recipeOwnerId: Int): LiveData<List<Note>>{
        return repository.getNotesFromRecipe(recipeOwnerId)
    }

    fun getAllNotes(): LiveData<List<Note>>{
        return repository.getAllNotes()
    }
}