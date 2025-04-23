package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.Ingredient
import uk.ac.aber.dcs.souschefapp.firebase.IngredientRepository
import uk.ac.aber.dcs.souschefapp.firebase.Log
import uk.ac.aber.dcs.souschefapp.firebase.Recipe
import uk.ac.aber.dcs.souschefapp.firebase.ShoppingItem
import uk.ac.aber.dcs.souschefapp.firebase.ShoppingRepository
import uk.ac.aber.dcs.souschefapp.firebase.UploadState

class ShoppingViewModel : ViewModel(){
    val ingredientRepository = IngredientRepository()
    val shoppingRepository = ShoppingRepository()

    private var listenerRegistration: ListenerRegistration? = null

    private val _uploadState = MutableLiveData<UploadState>(UploadState.Idle)
    val uploadState: LiveData<UploadState> = _uploadState

    private var _shoppingItems = MutableLiveData<List<ShoppingItem>>(emptyList())
    var shoppingItems: LiveData<List<ShoppingItem>> = _shoppingItems

    private var _compiledIngredients = MutableLiveData<List<Ingredient>>(emptyList())
    val compiledIngredients: LiveData<List<Ingredient>> = _compiledIngredients

    // Add a new item
    fun addItem(userId: String, content: String) {
        val item = ShoppingItem(content = content)
        viewModelScope.launch {
            try {
                shoppingRepository.addItem(userId, item)
            } catch (e: Exception) {
                android.util.Log.e("ShoppingViewModel", "Error adding item", e)
            }
        }
    }

    // Update an item (e.g. toggle checkbox)
    fun updateItem(userId: String, item: ShoppingItem) {
        viewModelScope.launch {
            try {
                shoppingRepository.updateItem(userId, item)
            } catch (e: Exception) {
                android.util.Log.e("ShoppingViewModel", "Error updating item", e)
            }
        }
    }

    // Delete an item
    fun deleteItem(userId: String, itemId: String) {
        viewModelScope.launch {
            try {
                shoppingRepository.deleteItem(userId, itemId)
            } catch (e: Exception) {
                android.util.Log.e("ShoppingViewModel", "Error deleting item", e)
            }
        }
    }

    fun startListening(userId: String) {
        listenerRegistration?.remove() // remove old listener
        listenerRegistration = shoppingRepository.listenToItems(userId) { items ->
            _shoppingItems.postValue(items)
        }
    }

    fun stopListening() {
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    fun syncShoppingList(userId: String?, newList: List<ShoppingItem>) {
        if (userId == null) return

        val currentList = _shoppingItems.value ?: emptyList()

        val currentMap = currentList.associateBy { it.itemId }
        val newMap = newList.associateBy { it.itemId }

        val itemsToAddOrUpdate = mutableListOf<ShoppingItem>()
        val itemsToDelete = mutableListOf<String>()

        // Determine which to add or update
        newList.forEach { newItem ->
            val existing = currentMap[newItem.itemId]
            if (existing == null || existing != newItem) {
                itemsToAddOrUpdate.add(newItem)
            }
        }

        // Determine which to delete
        currentList.forEach { existingItem ->
            if (!newMap.containsKey(existingItem.itemId)) {
                itemsToDelete.add(existingItem.itemId)
            }
        }

        // Perform the operations
        viewModelScope.launch {
            itemsToAddOrUpdate.forEach { item ->
                shoppingRepository.upsertItem(userId, item)
            }

            itemsToDelete.forEach { itemId ->
                shoppingRepository.deleteItem(userId, itemId)
            }
        }
    }

    fun fetchCompiledIngredients(userId: String?, logs: List<Log>) {
        if (userId == null) return

        _uploadState.value = UploadState.Loading
        val recipeMap: Map<String, Int> = logs  // id, multiple
            .flatMap { it.recipeIdList }
            .groupingBy { it }
            .eachCount()

        viewModelScope.launch {
            val newIngredients = mutableListOf<Ingredient>()
            recipeMap.forEach { (recipeId, multiple) ->
                val ingredients = ingredientRepository.getIngredients(userId, recipeId)

                val scaledIngredients = ingredients.map { ingredient ->
                    ingredient.copy(
                        quantity = multiplyQuantity(ingredient.quantity, multiple)
                    )
                }

                newIngredients.addAll(scaledIngredients)
            }
            addItems(userId, newIngredients)
            _compiledIngredients.postValue(newIngredients)
            _uploadState.value = UploadState.Success("Compilation complete!")
        }
    }

    private fun addItems(userId:String, ingredients: List<Ingredient>){
        viewModelScope.launch {
            ingredients.forEach{ingredient ->
                val shoppingItem = ShoppingItem(
                    content = buildString(ingredient)
                )
                shoppingRepository.addItem(userId, shoppingItem)
            }
        }
    }

    private fun buildString(ingredient: Ingredient): String{
        val ingredientText = buildString {
            append("\u2022 ")
            append("${ingredient.quantity} ")
            if (!ingredient.unit.isNullOrEmpty()) append("${ingredient.unit} ")
            append(ingredient.name)
            if (!ingredient.description.isNullOrEmpty()) append(" - ${ingredient.description}")
        }.trim()
        return ingredientText
    }

    private fun multiplyQuantity(quantity: String, multiple: Int): String{
        // Your logic to multiply the quantity by `count` (e.g., "2.5" * 3)
        val numericQuantity = quantity.toDoubleOrNull() ?: return quantity
        return (numericQuantity * multiple).toString()
    }
}