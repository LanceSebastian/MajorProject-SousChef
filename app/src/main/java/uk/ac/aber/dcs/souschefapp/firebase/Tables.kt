package uk.ac.aber.dcs.souschefapp.firebase

import com.google.firebase.Timestamp

data class Product(
    val createdBy: String,
    val name: String = "",
    val price: Double = 1.0,
)

data class Recipe(
    val recipeName: String,
    val instructionList: List<String> = emptyList<String>(),
    val tags: List<String> = emptyList<String>(), // Will need to think about this further
    val isActive: Boolean = true
)

// Subcollection to Recipe
data class Instruction(
    val content: String,
    val placement: Int = 0,
)

// Subcollection to Recipe
data class Ingredient(
    val ingredientId: Int = 0,
    val recipeOwnerId: Int,
    val name: String,
    val description: String,
    val quantity: Int,
    val unit: String
)

// Subcollection to Recipe
data class Note(
    val content: String,
    val timestamp: Long
)

// Subcollection to users
data class Log(
    val createdBy: String,
    val rating: Int = 0,
    val note: String? = null,
    val recipeIdList: List<Int> = emptyList(),
    val productIdList: List<Int> = emptyList()
)

