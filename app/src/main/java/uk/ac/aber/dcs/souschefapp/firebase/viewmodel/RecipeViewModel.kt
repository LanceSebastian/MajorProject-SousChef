package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.RecipeRepository
import uk.ac.aber.dcs.souschefapp.firebase.SelectMode

class RecipeViewModel : ViewModel() {
    private val recipeRepository = RecipeRepository()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _editMode = MutableLiveData(EditMode.View)
    val editMode: LiveData<EditMode> = _editMode

    private val _selectMode = MutableLiveData(SelectMode.View)
    val selectMode: LiveData<SelectMode> = _selectMode

    private var recipeListener: ListenerRegistration? = null

    private var _userRecipes = MutableLiveData<List<Recipe>>()
    var userRecipes: LiveData<List<Recipe>> = _userRecipes

    private var _searchedRecipes = MutableLiveData<List<Recipe>>()
    var searchedRecipes = _searchedRecipes

    private var _tagRecipes = MutableLiveData<List<Recipe>>()
    var tagRecipes = _tagRecipes

    private var selectRecipeId: String? = null
    private var _selectRecipe = MediatorLiveData<Recipe?>()
    var selectRecipe: LiveData<Recipe?> = _selectRecipe

    init {
        _selectRecipe.addSource(_userRecipes) { recipes ->
            selectRecipeId?.let { id ->
                _selectRecipe.value = recipes.find { it.recipeId == id }
            }
        }
    }

    fun setEditMode(newEditMode: EditMode){
        _editMode.value = newEditMode
    }

    fun setSelectMode(newSelectMode: SelectMode){
        _selectMode.value = newSelectMode
    }

    suspend fun createRecipeAndId(userId: String?, recipe: Recipe? = null, context: Context): String? {
        if (userId == null) {
            android.util.Log.e("RecipeViewModel", "Failed to create recipe due to null userId")
            return null
        }

        val standardRecipe = recipe?.copy(
            createdBy = userId
        ) ?: Recipe(
            name = "Unnamed",
            createdBy = userId,
        )

        val savedRecipe = recipeRepository.addRecipe(
            userId = userId,
            recipe = standardRecipe
        )

        return if (savedRecipe != null) {
            Toast.makeText(context, "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
            savedRecipe.recipeId
        } else {
            Toast.makeText(context, "Failed to save recipe.", Toast.LENGTH_SHORT).show()
            null
        }

    }

    fun createRecipe(userId: String?, recipe: Recipe? = null, context: Context) {
        if (userId == null) {
            android.util.Log.e("RecipeViewModel", "Failed to create recipe due to null userId")
            return
        }

        val standardRecipe = recipe?.copy(
            createdBy = userId
        ) ?: Recipe(
            name = "Unnamed",
            createdBy = userId,
        )

        viewModelScope.launch {
            val savedRecipe = recipeRepository.addRecipe(
                userId = userId,
                recipe = standardRecipe
            )

            if (savedRecipe != null) {
                Toast.makeText(context, "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
                savedRecipe.recipeId
            } else {
                Toast.makeText(context, "Failed to save recipe.", Toast.LENGTH_SHORT).show()

            }
        }

    }

    fun readRecipes(userId: String?) {
        if (userId == null) return

        _isLoading.postValue(true)

        recipeListener?.remove() // Stop previous listener if it exists

        recipeListener = recipeRepository.listenForRecipes(userId) { recipes ->
            _userRecipes.postValue(recipes)
            _isLoading.postValue(false)
        }
    }

    fun searchRecipe(userId: String?, query: String) {
        if (userId == null) return

        viewModelScope.launch {
            _searchedRecipes.value = recipeRepository.findRecipesByName(
                userId = userId,
                name = query
            )

            _tagRecipes.value = recipeRepository.findRecipesByTag(
                userId = userId,
                tag = query
            )
        }
    }

    fun selectRecipe(recipeId: String) {
        selectRecipeId = recipeId
        _userRecipes.value?.let { recipes ->
            _selectRecipe.value = recipes.find { it.recipeId == recipeId }
        }
    }

    fun clearSelectRecipe(){
        selectRecipeId = null
        _selectRecipe.value = null
    }

    fun stopListening(){
        recipeListener?.remove()
        recipeListener = null
    }

    fun updateRecipe(userId: String?, newRecipe: Recipe){
        val currentRecipe = _selectRecipe.value
        if (userId == null || currentRecipe == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.updateRecipeIfChanged(userId, currentRecipe, newRecipe)

            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to update recipe")
            }
        }

    }

    fun addTag(userId: String?, recipeId: String, tag: String){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.addTag(
                userId = userId,
                recipeId = recipeId,
                tag = tag
            )
            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to tag recipe")
            }
        }
    }

    fun updateTag(userId: String?, recipeId: String, oldTag: String, newTag: String){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.updateTag(
                userId = userId,
                recipeId = recipeId,
                oldTag = oldTag,
                newTag = newTag
            )
            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to tag recipe")
            }
        }
    }

    fun deleteTag(userId: String?, recipeId: String, tag: String){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.deleteTag(
                userId = userId,
                recipeId = recipeId,
                tag = tag
            )
            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to untag recipe")
            }
        }
    }

    fun updateInstructions(userId: String?, recipeId: String, instructions: List<String>){
        if (userId == null) return

        viewModelScope.launch {
            recipeRepository.updateInstructions(userId, recipeId, instructions)
        }
    }

    fun archiveRecipe(userId: String?, recipeId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.archiveRecipe(
                userId = userId,
                recipeId = recipeId,
            )
            val message = if (isSuccess) "Recipe archived successfully!" else "Failed to archive recipe."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreRecipe(userId: String?, recipeId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.restoreRecipe(
                userId = userId,
                recipeId = recipeId,
            )
            val message = if (isSuccess) "Recipe restored successfully!" else "Failed to restore recipe."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteRecipe(userId: String?, recipeId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = recipeRepository.deleteRecipe(
                userId = userId,
                recipeId = recipeId
            )

            val message = if (isSuccess) "Recipe deleted successfully!" else "Failed to delete recipe."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}