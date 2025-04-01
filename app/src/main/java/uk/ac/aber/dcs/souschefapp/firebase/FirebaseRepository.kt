package uk.ac.aber.dcs.souschefapp.firebase

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
                .document(log.date.toString()) // LogId is timestamp
                .set(log)
                .await()
            android.util.Log.d("Firestore", "Log added successfully")
            true
        } catch(e: Exception){
            android.util.Log.e("Firestore", "Error adding log: ${e.message}", e)
            false
        }
    }

    // Function to listen for logs in real-time for a specific user
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