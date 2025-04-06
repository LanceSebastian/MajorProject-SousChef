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
                    document.toObject(Product::class.java)  // This returns a nullable Product? object
                } ?: emptyList()

                onResult(products)  // Send the filtered products back to the caller via the callback
            }
    }

    suspend fun updateProduct(userId: String, product: Product): Boolean {
        return try {
            val productRef = db.collection("users")
                .document(userId)
                .collection("products")
                .document(product.productId)

            val updates = mapOf(
                "name" to product.name,
                "price" to product.price
            )

            productRef.update(updates).await()

            android.util.Log.d("Firestore", "Product updated successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error updating product ${e.message}", e)
            false
        }
    }

    suspend fun archiveProduct(userId: String, productId: String): Boolean {
        return try {
            val productRef = db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)
            // Firestore doesn't support direct updates to array elements, so we have to remove the old tag and add the new one
            productRef.update("isArchive", true).await()

            android.util.Log.d("Firestore", "Product archived successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error archiving product: ${e.message}", e)
            false
        }
    }

    suspend fun restoreProduct(userId: String, productId: String): Boolean {
        return try {
            val productRef = db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)
            // Firestore doesn't support direct updates to array elements, so we have to remove the old tag and add the new one
            productRef.update("isArchive", false).await()

            android.util.Log.d("Firestore", "Product archived successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error archiving product: ${e.message}", e)
            false
        }
    }

    suspend fun deleteProduct(userId: String, productId: String): Boolean {
        return try {
            val productRef = db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)

            // Get the document snapshot
            val snapshot = productRef.get().await()

            if (snapshot.exists()) {
                val isArchived = snapshot.getBoolean("isArchive") ?: false

                if (isArchived) {
                    // Delete the document if isArchived is true
                    productRef.delete().await()
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
                    android.util.Log.e("Firestore", "Error fetching recipes: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val recipes = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Recipe::class.java)  // This returns a nullable Recipe? object
                } ?: emptyList()

                onResult(recipes)  // Send the filtered recipes back to the caller via the callback
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
                val isArchived = snapshot.getBoolean("isArchive") ?: false

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

class NoteRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addNote(userId: String, recipeId: String, note: Note): Boolean {
        return try {
            // Fetch the recipe name
            val recipeSnapshot = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .get()
                .await()

            val recipeName = recipeSnapshot.getString("recipeName") ?: "Unknown Recipe"  // Default if not found

            // Create a new Note with recipeName
            val newNote = note.copy(recipeName = recipeName)

            // Save the note to the "notes" subcollection
            db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("notes")
                .add(newNote)
                .await()

            android.util.Log.d("Firestore", "Note added successfully with recipe name: $recipeName")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error adding note: ${e.message}", e)
            false
        }
    }

    fun listenForNotes(userId: String, recipeId: String, onResult: (List<Note>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("recipes")
            .document(recipeId)
            .collection("notes")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching notes: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val notes = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Note::class.java)  // This returns a nullable Note? object
                } ?: emptyList()

                onResult(notes)  // Send the filtered notes back to the caller via the callback
            }
    }

    suspend fun updateNote(userId: String, recipeId: String, note: Note): Boolean {
        return try{
            db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("notes")
                .document(note.noteId)
                .update("content", note.content)
                .await()
            android.util.Log.d("Firestore", "Note updated successfully")
            true
        } catch (e: Exception){
            android.util.Log.e("Firestore", "Error updating note: ${e.message}", e)
            false
        }
    }

    suspend fun deleteNote(userId: String, recipeId: String, noteId: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("notes")
                .document(noteId)
                .delete()
                .await()
            android.util.Log.d("Firestore", "Note deleted successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error deleting note: ${e.message}", e)
            false
        }
    }
}

class IngredientRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addIngredient(userId: String, recipeId: String, ingredient: Ingredient): Boolean {
        return try{
            val ingredientRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("ingredients")
                .document()
            val newId = ingredientRef.id

            val newIngredient = ingredient.copy(ingredientId = newId)

            ingredientRef.set(newIngredient).await()
            android.util.Log.d("Firestore", "Ingredient added successfully")
            true
        } catch (e: Exception){
            android.util.Log.e("Firestore", "Error adding ingredient: ${e.message}", e)
            false
        }
    }

    fun listenForIngredients(userId: String, recipeId: String, onResult: (List<Ingredient>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("recipes")
            .document(recipeId)
            .collection("ingredients")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching ingredients: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val ingredients = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Ingredient::class.java)  // This returns a nullable Ingredient? object
                } ?: emptyList()

                onResult(ingredients)  // Send the filtered ingredients back to the caller via the callback
            }
    }

    suspend fun updateIngredient(userId: String, recipeId: String, ingredient: Ingredient): Boolean {
        return try {
            val ingredientRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("ingredients")
                .document()

            val updates = mapOf(
                "name" to ingredient.name,
                "description" to ingredient.description,
                "quanitity" to ingredient.quantity,
                "unit" to ingredient.unit
            )

            ingredientRef.update(updates).await()

            android.util.Log.d("Firestore", "Ingredient updated successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error updating ingredient ${e.message}", e)
            false
        }
    }

    suspend fun deleteIngredient(userId: String, recipeId: String, ingredientId: String): Boolean {
        return try {
            db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("ingredients")
                .document(ingredientId)
                .delete()
                .await()
            android.util.Log.d("Firestore", "Ingredient deleted successfully")
            true
        } catch (e: Exception) {
            android.util.Log.e("Firestore", "Error deleting ingredient: ${e.message}", e)
            false
        }
    }
}

class InstructionRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun addInstruction(userId: String, recipeId: String, instruction: Instruction): Boolean {
        return try{
            val instructionRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("instructions")
                .document()
            val newId = instructionRef.id

            val newInstruction = instruction.copy(instructionId = newId)

            instructionRef.set(newInstruction).await()
            android.util.Log.d("Firestore", "Instruction added successfully")
            true
        } catch (e: Exception){
            android.util.Log.e("Firestore", "Error adding instruction: ${e.message}", e)
            false
        }
    }

    fun listenForInstructions(userId: String, recipeId: String, onResult: (List<Instruction>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(userId)
            .collection("recipes")
            .document(recipeId)
            .collection("instructions")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    android.util.Log.e("Firestore", "Error fetching instructions: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val instructions = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Instruction::class.java)  // This returns a nullable Ingredient? object
                } ?: emptyList()

                onResult(instructions)  // Send the filtered ingredients back to the caller via the callback
            }
    }

    suspend fun updateContent(userId: String, recipeId: String, instructionId: String, content: String): Boolean {
        return try{
            val instructionRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("instructions")
                .document(instructionId)
            instructionRef.update("content", content).await()
            android.util.Log.d("Firestore", "Instruction content updated successfully")
            true
        } catch (e:Exception){
            android.util.Log.e("Firestore", "Error updating instruction content: ${e.message}", e)
            false
        }
    }

    suspend fun updatePlacement(userId: String, recipeId: String, instructionId: String, placement: Int): Boolean {
        TODO("""
            I need to also update the rest of the instructions. I should think about how I'm going to structure this for 
            effective server calls.
            Should I:
                1) place instruction: recursively update the placements of the other instructions - #instructions calls)
                2) use an array for instructions and update the array - one call (I think)
            |
        """)

    }


    suspend fun deleteInstruction(userId: String, recipeId: String, instructionId: String): Boolean {
        return try{
            val instructionRef = db.collection("users")
                .document(userId)
                .collection("recipes")
                .document(recipeId)
                .collection("instructions")
                .document(instructionId)
                .delete()
                .await()
            android.util.Log.d("Firestore", "Instruction deleted successfully")
            true
        } catch (e:Exception){
            android.util.Log.e("Firestore", "Error deleting instruction: ${e.message}", e)
            false
        }
    }
}