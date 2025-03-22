package uk.ac.aber.dcs.souschefapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Recipe

/*      Handles Recipe Manipulation and Creation       */
class RecipeViewModel(
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)

    fun insertRecipe(recipe: Recipe){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertRecipe(recipe)
        }
    }

    fun updateRecipe(recipe: Recipe){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateRecipe(recipe)
        }
    }

    fun deactivateRecipe(recipeId: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deactivateRecipe(recipeId)
        }
    }

    fun getAllRecipes(): LiveData<List<Recipe>>{
        return repository.getAllRecipes()
    }

    fun getRecipeById(recipeId: Int): LiveData<Recipe>{
        return repository.getRecipeById(recipeId)
    }

    fun getRecipeByName(recipeName: String): LiveData<Recipe> {
        return repository.getRecipeByName(recipeName)
    }
}