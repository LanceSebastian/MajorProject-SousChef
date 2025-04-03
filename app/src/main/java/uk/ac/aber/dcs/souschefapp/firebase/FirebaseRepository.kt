package uk.ac.aber.dcs.souschefapp.firebase

import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.Listen
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class LogRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addLog(userId: String, log: Log): Boolean{
        return try {
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(log.logId)
                .set(log)
                .await()
            android.util.Log.d("Firestore", "Log added successfully")
            true
        } catch(e: Exception){
            android.util.Log.e("Firestore", "Error adding log: ${e.message}", e)
            false
        }
    }

    fun listenForLogs(userId: String, onResult: (List<Log>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("logs")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching logs: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val logs = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Log::class.java)  // This returns a nullable Post? object
                } ?: emptyList()

                onResult(logs)  // Send the filtered posts back to the caller via the callback
            }
    }

    fun listenForSelectedLogs(userId: String, startDate: Timestamp, endDate: Timestamp, onResult: (List<Log>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("logs")
            .whereGreaterThanOrEqualTo("createdBy", startDate)
            .whereLessThan("createdBy", endDate)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching logs: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val logs = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Log::class.java)  // This returns a nullable Post? object
                } ?: emptyList()

                onResult(logs)  // Send the filtered posts back to the caller via the callback
            }
    }

    suspend fun addRecipeToLog(userId: String, logId: String, recipeId: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)

            logRef.update("recipeIdList", FieldValue.arrayUnion(recipeId)).await()
            android.util.Log.d("Firestore", "Recipe added successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding recipe: ${e.message}", e)
            false
        }
    }

    suspend fun removeRecipeFromLog(userId:String, logId: String, recipeId: String): Boolean{
        return try{
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)
                .update("recipeIdList", FieldValue.arrayRemove(recipeId))
                .await()
            android.util.Log.d("Firestore", "Recipe removed from Log successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error removing recipe: ${e.message}", e)
            false
        }
    }

    suspend fun addProductToLog(userId: String, logId: String, productId: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)

            logRef.update("productIdList", FieldValue.arrayUnion(productId)).await()
            android.util.Log.d("Firestore", "Product added successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding product: ${e.message}", e)
            false
        }
    }

    suspend fun removeProductFromLog(userId: String, logId: String, productId: String): Boolean{
        return try{
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)
                .update("productIdList", FieldValue.arrayRemove(productId))
                .await()
            android.util.Log.d("Firestore", "Product removed from Log successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error removing product: ${e.message}", e)
            false
        }
    }

    suspend fun updateLogRating(userId:String, logId: String, rating: Int): Boolean{
        return try{
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)
                .update("rating", rating)
                .await()
            android.util.Log.d("Firestore", "Rating updated successfully")
            true
        } catch (e: Exception){
            android.util.Log.e("Firestore", "Error updating rating: ${e.message}", e)
            false
        }
    }

    suspend fun updateLogNote(userId: String, logId: String, note: String): Boolean{
        return try{
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)
                .update("note", note)
                .await()
            android.util.Log.d("Firestore", "Note updated successfully")
            true
        } catch (e: Exception){
            android.util.Log.e("Firestore", "Error updating note: ${e.message}", e)
            false
        }
    }

    suspend fun deleteLog(userId: String, logId: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId)
                .delete()
                .await()
            android.util.Log.d("Firestore", "Log deleted successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error deleting log: ${e.message}", e)
            false
        }
    }
}

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addProduct(userId: String, product: Product): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("products")
                .add(product)
                .await()
            android.util.Log.d("Firestore", "Product added successfully")
            true
        } catch(e: Exception){
            android.util.Log.e("Firestore", "Error adding product: ${e.message}", e)
            false
        }
    }

    fun listenForProducts(userId: String, onResult: (List<Product>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("products")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching products: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val products = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Product::class.java)  // This returns a nullable Post? object
                } ?: emptyList()

                onResult(products)  // Send the filtered posts back to the caller via the callback
            }
    }

    suspend fun updateProduct(userId: String, product: Product): Boolean {
        TODO()
    }

    suspend fun archiveProduct(userId: String, productId: String): Boolean {
        TODO()
    }

    suspend fun restoreProduct(userId: String, productId: String): Boolean {
        TODO()
    }

    suspend fun deleteProduct(userId: String, productId: String): Boolean {
        TODO()
    }

}

class RecipeRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addRecipe(userId: String, recipe: Recipe): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("recipes")
                .add(recipe)
                .await()
            android.util.Log.d("Firestore", "Recipe added successfully")
            true
        } catch(e: Exception){
            android.util.Log.e("Firestore", "Error adding recipe: ${e.message}", e)
            false
        }
    }

    fun listenForRecipes(userId: String, onResult: (List<Recipe>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("recipes")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching logs: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val recipes = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Recipe::class.java)  // This returns a nullable Post? object
                } ?: emptyList()

                onResult(recipes)  // Send the filtered posts back to the caller via the callback
            }
    }

    suspend fun findRecipesByTag(userId: String, tag: String): List<Recipe> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("recipes")
                .whereArrayContains("tags", tag)
                .get()
                .await()

            val recipes = snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
            android.util.Log.d("Firestore", "Recipes found: ${recipes.size}")
            recipes
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error finding recipes by tag: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun findRecipesByName(userId: String, name: String): List<Recipe> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("recipes")
                .whereArrayContains("name", name)
                .get()
                .await()

            val recipes = snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
            android.util.Log.d("Firestore", "Recipes found: ${recipes.size}")
            recipes
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error finding recipes by tag: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun addTag(userId: String, recipeId: String, tag: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            logRef.update("tags", FieldValue.arrayUnion(tag)).await()
            android.util.Log.d("Firestore", "Tag added successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding tag: ${e.message}", e)
            false
        }
    }

    suspend fun updateTag(userId: String, recipeId: String, oldTag: String, newTag: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            // Firestore doesn't support direct updates to array elements, so we have to remove the old tag and add the new one
            logRef.update("tags", FieldValue.arrayRemove(oldTag)).await()
            logRef.update("tags", FieldValue.arrayUnion(newTag)).await()

            android.util.Log.d("Firestore", "Tag updated successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error updating tag: ${e.message}", e)
            false
        }
    }

    suspend fun findTag(userId: String, recipeId: String, tag: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            val snapshot = logRef.get().await()

            if (snapshot.exists()) {
                val tags = snapshot.get("tags")
                if (tags is List<*>) {
                    tags.filterIsInstance<String>().contains(tag)
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error finding tag: ${e.message}", e)
            false
        }
    }

    suspend fun deleteTag(userId: String, recipeId: String, tag: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            logRef.update("tags", FieldValue.arrayRemove(tag)).await()
            android.util.Log.d("Firestore", "Tag added successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding tag: ${e.message}", e)
            false
        }
    }

    suspend fun archiveRecipe(userId: String, recipeId: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            logRef.update("isArchive", true).await()
            android.util.Log.d("Firestore", "Recipe archived successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error archiving recipe: ${e.message}", e)
            false
        }
    }

    suspend fun restoreRecipe(userId: String, recipeId: String): Boolean {
        return try {
            val logRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            logRef.update("isArchive", false).await()
            android.util.Log.d("Firestore", "Recipe archived successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error archiving recipe: ${e.message}", e)
            false
        }
    }

    suspend fun deleteRecipe(userId: String, recipeId: String): Boolean {
        return try {
            val recipeRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)

            // Get the document snapshot
            val snapshot = recipeRef.get().await()

            if (snapshot.exists()) {
                val isArchived = snapshot.getBoolean("isArchived") ?: false

                if (isArchived) {
                    // Delete the document if isArchived is true
                    recipeRef.delete().await()
                    android.util.Log.d("Firestore", "Recipe deleted successfully")
                    true
                } else {
                    android.util.Log.d("Firestore", "Recipe is not archived, cannot be deleted")
                    false
                }
            } else {
                android.util.Log.d("Firestore", "Recipe does not exist")
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error deleting recipe: ${e.message}", e)
            false
        }
    }
}