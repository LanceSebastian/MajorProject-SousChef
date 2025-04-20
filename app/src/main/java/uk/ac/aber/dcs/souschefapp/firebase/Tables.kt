package uk.ac.aber.dcs.souschefapp.firebase

import com.google.firebase.Timestamp

data class Product(
    val productId: String = "",
    val imageUrl: String? = null,
    val createdBy: String = "",
    val name: String = "",
    val price: Double = 1.0,
    val isArchive: Boolean = false
)

data class Recipe(
    val recipeId: String = "",
    val imageUrl: String? = null,
    val name: String = "",
    val createdBy: String = "",
    val instructions: List<String>? = null,
    val tags: List<String>? = null,
    val isArchive: Boolean = false
)

// Subcollection to Recipe
data class Ingredient(
    val ingredientId: String = "",
    val name: String = "",
    val description: String? = null,
    val quantity: String = "",
    val unit: String? = null,
)

// Subcollection to Recipe
data class Note(
    val noteId: String = "",
    val recipeName: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()
)

// Subcollection to users
data class Log(
    val logId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    val rating: Int = 0,
    val note: String? = null,
    val recipeIdList: List<String> = emptyList(),
    val productIdList: List<String> = emptyList()
){
    constructor() : this("",Timestamp.now(), "",0, null, emptyList(), emptyList())
}

