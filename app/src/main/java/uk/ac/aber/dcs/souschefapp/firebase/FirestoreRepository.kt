package uk.ac.aber.dcs.souschefapp.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    /*      Log     */
    suspend fun addLog(userId: String, log: Log, logId: String): Boolean{
        return try {
            db.collection("users")
                .document(userId)
                .collection("logs")
                .document(logId) // LogId is timestamp
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

    fun addRecipeToLog(userId: String, logId: String, recipeId: String, callback: (Boolean) -> Unit) {
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("recipeIdList", FieldValue.arrayUnion(recipeId))
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Recipe added to Log successfully")
                callback(true)
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error adding recipe: ${e.message}", e)
                callback(false)
            }
    }

    fun removeRecipeFromLog(userId:String, logId: String, recipeId: String, callback: (Boolean) -> Unit){
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("recipeIdList", FieldValue.arrayRemove(recipeId))
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Recipe removed from Log successfully")
                callback(true)
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error removing recipe: ${e.message}", e)
                callback(false)
            }
    }

    fun addProductToLog(userId: String, logId: String, productId: String, callback: (Boolean) -> Unit) {
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("productIdList", FieldValue.arrayUnion(productId))
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Product added to Log successfully")
                callback(true)
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error adding product: ${e.message}", e)
                callback(false)
            }
    }

    fun removeProductFromLog(userId: String, logId: String, productId: String, callback: (Boolean) -> Unit){
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("productIdList", FieldValue.arrayRemove(productId))
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Product removed from Log successfully")
                callback(true)
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error removing product: ${e.message}", e)
                callback(false)
            }
    }

    fun updateLogRating(userId:String, logId: String, rating: Int){
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("rating", rating)
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Rating updated successfully")
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error updating rating: ${e.message}", e)
            }
    }

    fun updateLogNote(userId: String, logId: String, note: String){
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.update("note", note)
            .addOnSuccessListener {
                android.util.Log.d("Firestore", "Note updated successfully")
            }
            .addOnFailureListener{ e ->
                android.util.Log.e("Firestore", "Error updating note: ${e.message}", e)
            }
    }

    fun deleteLog(userId: String, logId: String, callback: (Boolean) -> Unit){
        val logRef = db.collection("users")
            .document(userId)
            .collection("logs")
            .document(logId)

        logRef.delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener{ e ->
                callback(false)
            }
    }
}