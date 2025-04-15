package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.Ingredient
import uk.ac.aber.dcs.souschefapp.firebase.IngredientRepository

class IngredientViewModel : ViewModel() {
    private val ingredientRepository = IngredientRepository()

    private var ingredientListener: ListenerRegistration? = null

    private var _recipeIngredients = MutableLiveData<List<Ingredient>>()
    var recipeIngredient: LiveData<List<Ingredient>> = _recipeIngredients

    fun createIngredient(userId: String?, recipeId: String?, ingredient: Ingredient){
        if (userId == null || recipeId == null) return

        viewModelScope.launch {
            val isSuccess = ingredientRepository.addIngredient(userId, recipeId, ingredient)

            if (!isSuccess) {
                android.util.Log.e("IngredientViewModel", "Failed to create ingredient")
            }
        }
    }

    // Method updates existing and creates new ingredients, deleting old.
    fun updateIngredients(userId: String?, recipeId: String?, ingredients: List<Ingredient>){
        if (userId == null || recipeId == null) return

        viewModelScope.launch {
            val existingIngredients = _recipeIngredients.value ?: emptyList()

            // Delete
            val toDelete = existingIngredients.filter{ existing ->
                ingredients.none { it.ingredientId == existing.ingredientId }
            }

            toDelete.forEach{ ingredient ->
                val deleted = ingredientRepository.deleteIngredient(userId, recipeId, ingredient.ingredientId)
                if (!deleted) {
                    android.util.Log.e("IngredientViewModel", "Failed to delete ingredient: ${ingredient.name}")
                }
            }

            // Create and Update
            ingredients.forEach { ingredient ->
                val existing = _recipeIngredients.value?.find { it.ingredientId == ingredient.ingredientId }

                val isSuccess = if (existing != null) {
                    if (existing != ingredient) ingredientRepository.updateIngredient(userId, recipeId, ingredient) else true
                } else {
                    ingredientRepository.addIngredient(userId, recipeId, ingredient)
                }

                if (!isSuccess) {
                    android.util.Log.e("IngredientViewModel", "Failed to add/update ingredient: ${ingredient.name}")
                }
            }
        }
    }

    fun readIngredients(userId: String?, recipeId: String?){
        if (userId == null || recipeId == null) return

        ingredientListener?.remove()

        ingredientListener = ingredientRepository.listenForIngredients(userId, recipeId) { ingredients ->
            _recipeIngredients.postValue(ingredients)
        }
    }

    fun stopListening(){
        ingredientListener?.remove()
        ingredientListener = null
    }

    fun updateIngredient(userId: String?, recipeId: String?, ingredient: Ingredient){
        if (userId == null || recipeId == null) return

        viewModelScope.launch{
            val isSuccess = ingredientRepository.updateIngredient(userId, recipeId, ingredient)
            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to update recipe")
            }
        }
    }

    fun deleteIngredient(userId: String?, recipeId: String?, ingredientId: String){
        if (userId == null || recipeId == null) return

        viewModelScope.launch {
            val isSuccess = ingredientRepository.deleteIngredient(userId, recipeId, ingredientId)
            if (!isSuccess) {
                android.util.Log.e("RecipeViewModel", "Failed to delete recipe")
            }
        }
    }
}