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
                android.util.Log.e("RecipeViewModel", "Failed to create recipe")
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