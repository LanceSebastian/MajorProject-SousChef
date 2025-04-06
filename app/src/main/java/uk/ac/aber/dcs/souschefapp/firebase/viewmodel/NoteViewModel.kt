package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.Note
import uk.ac.aber.dcs.souschefapp.firebase.NoteRepository

class NoteViewModel : ViewModel() {
    private val noteRepository = NoteRepository()

    private var noteListener: ListenerRegistration? = null

    private var _notes = MutableLiveData<List<Note>>() // Single recipe notes.
    var notes: LiveData<List<Note>> = _notes

    private var _compiledNotes = MutableLiveData<List<Note>>(emptyList()) // Pair(recipeName, Note)
    var compiledNotes: LiveData<List<Note>> = _compiledNotes

    fun createNote(userId: String?, recipeId: String, note: Note, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = noteRepository.addNote(
                userId = userId,
                recipeId = recipeId,
                note = note
            )
            val message = if (isSuccess) "Note added successfully!" else "Failed to add note."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun readNotesFromRecipe(userId: String?, recipeId: String){
        if (userId == null) return

        noteListener?.remove() // Stop previous listener if it exists

        noteListener = noteRepository.listenForNotes(userId, recipeId) { notes ->
            _notes.postValue(notes)
        }
    }

    fun compileNotesFromRecipes(userId: String, listOfRecipeIds: List<String>){
        val notesDeferred  = mutableListOf<Deferred<List<Note>>>()
        listOfRecipeIds.forEach{ recipeId ->
            val deferred = viewModelScope.async {
                noteRepository.getNotes(
                    userId = userId,
                    recipeId = recipeId
                )
            }
            notesDeferred.add(deferred)
        }

        viewModelScope.launch {
            val compiledNotes = mutableListOf<Note>()
            notesDeferred.forEach { deferred ->
                val notes = deferred.await()
                compiledNotes.addAll(notes)
            }

            _compiledNotes.postValue(compiledNotes)
        }
    }

    fun emptyCompiledNotes(){
        _compiledNotes.postValue(emptyList())
    }

    fun updateNote(userId: String?, recipeId: String, note: Note){
        if (userId == null) return

        viewModelScope.launch {
            noteRepository.updateNote(
                userId = userId,
                recipeId = recipeId,
                note = note
            )
        }
    }

    fun deleteNote(userId: String?, recipeId: String, noteId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = noteRepository.deleteNote(
                userId = userId,
                recipeId = recipeId,
                noteId = noteId
            )
            val message = if (isSuccess) "Note deleted successfully!" else "Failed to delete note."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}