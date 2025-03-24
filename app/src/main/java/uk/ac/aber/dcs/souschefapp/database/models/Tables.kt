package uk.ac.aber.dcs.souschefapp.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "accounts"
)
data class Account(
    @PrimaryKey(autoGenerate = true) val accountId: Int = 0,
    val username: String,
    val password: String,
    val email: String,
    val isActive: Boolean = true
)

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["accountId"],
        childColumns = ["accountOwnerId"]
    )]
)
data class Product(
    @PrimaryKey(autoGenerate = true) val productId: Int = 0,
    val accountOwnerId: Int,
    val name: String = "",
    val price: BigDecimal = BigDecimal(0),
)

@Entity(
    tableName = "recipes"
)
data class Recipe(
    @PrimaryKey(autoGenerate = true) val recipeId: Int = 0,
    val recipeName: String,
    val description: String = "", // Currently unused
    val instructionList: List<String> = emptyList<String>(),
    val tags: List<String> = emptyList<String>(), // Will need to think about this further
    val isActive: Boolean = true
)

@Entity(
    tableName = "ingredients",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = ["recipeId"],
        childColumns = ["recipeOwnerId"]
    )]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val ingredientId: Int = 0,
    val recipeOwnerId: Int,
    val name: String,
    val description: String,
    val quantity: Int,
    val unit: String
)

// Log is the daily info
@Entity(
    tableName = "logs",
    foreignKeys = [ForeignKey(
        entity = Account::class,
        parentColumns = ["accountId"],
        childColumns = ["accountOwnerId"]
    )]
)
data class Log(
    @PrimaryKey(autoGenerate = true) val logId: Int = 0,
    val accountOwnerId: Int,
    val date: Long,
    val rating: Int = 0,
    val recipeIdList: List<Int> = emptyList(),
    val productIdList: List<Int> = emptyList(),
    val note: String = ""
)

// Note is for each recipe
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = ["recipeId"],
        childColumns = ["recipeOwnerId"]
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val recipeOwnerId: Int,
    val content: String,
    val date: Long
)

@Entity(
    tableName = "receipts",
    foreignKeys = [ForeignKey(
        entity = Log::class,
        parentColumns = ["logId"],
        childColumns = ["logOwnerId"]
    )]
)
data class Receipt(
    @PrimaryKey(autoGenerate = true) val receiptId: Int = 0,
    val logOwnerId: Int,
    val description: String,
)

