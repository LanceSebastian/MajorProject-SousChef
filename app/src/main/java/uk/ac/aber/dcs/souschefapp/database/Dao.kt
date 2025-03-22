package uk.ac.aber.dcs.souschefapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.database.models.Ingredient
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.database.models.Recipe

@Dao
interface AccountDao{
    @Insert
    suspend fun insertAccount(account: Account)

    @Query("UPDATE accounts SET isActive = 0 WHERE accountId = :accountId")
    suspend fun deactivateAccount(accountId: Int)

    @Update
    suspend fun updateAccount(account: Account)

    @Query("SELECT * FROM accounts WHERE accountId = :accountId")
    fun getAccountById(accountId: Int): LiveData<Account>

    @Query("SELECT * FROM accounts WHERE username = :username")
    fun getAccountByUserName(username: String): LiveData<Account>

    @Query("SELECT * FROM accounts WHERE email = :email")
    fun getAccountByEmail(email: String): LiveData<Account>

    @Query("SELECT * FROM accounts WHERE username = :username AND password = :password")
    fun loginAccount(username:String, password:String): LiveData<Account>
}

@Dao
interface ProductDao{
    @Insert
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products WHERE productId = :productId")
    suspend fun deleteProduct(productId: Int)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("SELECT * FROM products WHERE accountOwnerId = :accountOwnerId")
    fun getAllProductsFromAccount(accountOwnerId: Int): LiveData<List<Product>>

}

@Dao
interface RecipeDao{
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Query("UPDATE recipes SET isActive = 0 WHERE recipeId = :recipeId")
    suspend fun deactivateRecipe(recipeId: Int)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes WHERE isActive = 1")
    fun getAllRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE recipeId = :recipeId")
    fun getRecipeById(recipeId: Int): LiveData<Recipe>

    @Query("SELECT * FROM recipes WHERE recipeName = :recipeName")
    fun getRecipeByName(recipeName: String): LiveData<Recipe>
}

@Dao
interface IngredientDao{
    @Insert
    suspend fun insertIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    @Query("DELETE FROM ingredients WHERE ingredientId = :ingredientId")
    suspend fun deleteIngredient(ingredientId: Int)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Query("SELECT * FROM ingredients WHERE recipeOwnerId = :recipeOwnerId")
    fun getIngredientsFromRecipe(recipeOwnerId: Int): LiveData<List<Ingredient>>
}

@Dao
interface LogDao{
    @Insert
    suspend fun insertLog(log: Log)

    @Delete
    suspend fun deleteLog(log: Log)

    @Query("DELETE FROM logs WHERE logId = :logId")
    suspend fun deleteLog(logId: Int)

    @Update
    suspend fun updateLog(log: Log)

    @Query("SELECT * FROM logs WHERE date = :date")
    fun getLogFromDate(date: Long): LiveData<Log>

    @Query("SELECT * FROM logs WHERE accountOwnerId = :accountOwnerId")
    fun getAllLogsFromAccount(accountOwnerId: Int): LiveData<List<Log>>
}

@Dao
interface NoteDao{
    @Insert
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNote(noteId: Int)

    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM notes WHERE recipeOwnerId = :recipeOwnerId")
    fun getNotesFromRecipe(recipeOwnerId: Int): LiveData<List<Note>>

    @Query("SELECT * FROM notes")
    fun getAllNotes(): LiveData<List<Note>>
}
