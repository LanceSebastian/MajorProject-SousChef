package uk.ac.aber.dcs.souschefapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.models.Ingredient

class IngredientViewModel (
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)

    fun insertIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredientId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteIngredient(ingredientId)
        }
    }
    fun updateIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIngredient(ingredient)
        }
    }

    fun getIngredientsFromRecipe(recipeOwnerId: Int): LiveData<List<Ingredient>>{
        return repository.getIngredientsFromRecipe(recipeOwnerId)
    }
}