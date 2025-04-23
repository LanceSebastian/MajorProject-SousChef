package uk.ac.aber.dcs.souschefapp.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Product(
    val productId: String = "",
    val imageUrl: String? = null,
    val createdBy: String = "",
    val name: String = "",
    val price: Double = 1.0,
    @PropertyName("archive") // Ensure Firestore maps to `isArchive` here
    val isArchive: Boolean = false
)

data class Recipe(
    val recipeId: String = "",
    val imageUrl: String? = null,
    val name: String = "",
    val createdBy: String = "",
    val instructions: List<String>? = null,
    val tags: List<String>? = null,
    @PropertyName("archive") // Ensure Firestore maps to `isArchive` here
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

data class Receipt(
    val storeName: String = "",
    val date: Timestamp = Timestamp.now(), // ISO 8601 String is safest (use Instant or LocalDateTime if you serialize manually)
    val items: List<ReceiptItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String? = null,
    val rawText: String = ""
)

data class ReceiptItem(
    val name: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)

data class ShoppingItem(
    val itemId: String = "",
    val checked: Boolean = false,
    val content: String = ""
)


