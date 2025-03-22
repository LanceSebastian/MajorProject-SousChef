package uk.ac.aber.dcs.souschefapp.database

import android.app.Application
import androidx.lifecycle.LiveData
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.database.models.Ingredient
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.database.models.Recipe

class SousChefRepository(application: Application) {
    val database: SousChefDatabase by lazy { SousChefDatabase.getInstance(application) }
    private val accountDao = database.accountDao()
    private val productDao = database.productDao()
    private val recipeDao = database.recipeDao()
    private val ingredientDao = database.ingredientDao()
    private val logDao = database.logDao()
    private val noteDao = database.noteDao()

    /*      Account     */
    suspend fun insertAccount(account: Account){
        accountDao.insertAccount(account)
    }

    suspend fun deactivateAccount(accountId: Int){
        accountDao.deactivateAccount(accountId)
    }

    suspend fun updateAccount(account: Account){
        accountDao.updateAccount(account)
    }

    fun getAccountById(accountId: Int) : LiveData<Account> {
        return accountDao.getAccountById(accountId)
    }

    fun getAccountByUsername(username: String) : LiveData<Account> {
        return accountDao.getAccountByUserName(username)
    }

    fun getAccountByEmail(email: String) : LiveData<Account> {
        return accountDao.getAccountByEmail(email)
    }

    fun login(username: String, password: String): LiveData<Account> {
        return accountDao.loginAccount(username, password)
    }

    /*      Product     */
    suspend fun insertProduct(product: Product){
        productDao.insertProduct(product)
    }

    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }

    suspend fun deleteProduct(productId: Int){
        productDao.deleteProduct(productId)
    }

    suspend fun updateProduct(product: Product){
        productDao.updateProduct(product)
    }

    fun getAllProductsFromAccount(accountId: Int): LiveData<List<Product>>{
        return productDao.getAllProductsFromAccount(accountId)
    }

    /*      Recipe     */
    suspend fun insertRecipe(recipe: Recipe){
        recipeDao.insertRecipe(recipe)
    }

    suspend fun deactivateRecipe(recipeId: Int){
        recipeDao.deactivateRecipe(recipeId)
    }

    suspend fun updateRecipe(recipe: Recipe){
        recipeDao.updateRecipe(recipe)
    }

    fun getAllRecipes(): LiveData<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

    fun getRecipeById(recipeId: Int): LiveData<Recipe>{
        return recipeDao.getRecipeById(recipeId)
    }

    fun getRecipeByName(recipeName: String): LiveData<Recipe>{
        return recipeDao.getRecipeByName(recipeName)
    }

    /*      Ingredient     */
    suspend fun insertIngredient(ingredient: Ingredient){
        ingredientDao.insertIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient){
        ingredientDao.deleteIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredientId: Int){
        ingredientDao.deleteIngredient(ingredientId)
    }

    suspend fun updateIngredient(ingredient: Ingredient){
        ingredientDao.updateIngredient(ingredient)
    }

    fun getIngredientsFromRecipe(recipeOwnerId: Int): LiveData<List<Ingredient>>{
        return ingredientDao.getIngredientsFromRecipe(recipeOwnerId)
    }

    /*      Log     */
    suspend fun insertLog(log: Log){
        logDao.insertLog(log)
    }

    suspend fun deleteLog(log: Log){
        logDao.deleteLog(log)
    }

    suspend fun deleteLog(logId: Int){
        logDao.deleteLog(logId)
    }

    suspend fun updateLog(log: Log){
        logDao.updateLog(log)
    }

    fun getLogFromDate(date: Long): LiveData<Log>{
        return logDao.getLogFromDate(date)
    }

    fun getAllLogsFromAccount(accountOwnerId: Int): LiveData<List<Log>>{
        return logDao.getAllLogsFromAccount(accountOwnerId)
    }

    /*      Note     */
    suspend fun insertNote(note: Note){
        noteDao.insertNote(note)
    }

    suspend fun deleteNote(note: Note){
        noteDao.deleteNote(note)
    }

    suspend fun deleteNote(noteId: Int){
        noteDao.deleteNote(noteId)
    }

    suspend fun updateNote(note: Note){
        noteDao.updateNote(note)
    }

    fun getNotesFromRecipe(recipeOwnerId: Int): LiveData<List<Note>>{
        return noteDao.getNotesFromRecipe(recipeOwnerId)
    }

    fun getAllNotes(): LiveData<List<Note>>{
        return noteDao.getAllNotes()
    }
}