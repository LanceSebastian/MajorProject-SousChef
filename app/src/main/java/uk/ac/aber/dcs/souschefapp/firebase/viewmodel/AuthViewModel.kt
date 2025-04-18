package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _username = MutableLiveData<String?>()
    val username: LiveData<String?> get() = _username

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingAutoLogin = MutableLiveData(true) // Track auto-login loading state
    val isLoadingAutoLogin: LiveData<Boolean> = _isLoadingAutoLogin

    init {
        // Start checking the user authentication status on initialization
        checkAutoLogin()
    }

    // Auto-login check (user already logged in?)
    private fun checkAutoLogin() {
        _isLoadingAutoLogin.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already logged in
            _user.value = currentUser
            _authStatus.value = "User auto-logged in!"
            _user.value?.let{ fetchUsername(it.uid) }
        } else {
            // No user logged in
            _authStatus.value = "No user logged in"
        }
        _isLoadingAutoLogin.value = false
    }

    fun register(email: String, password: String, username: String) {
        _isLoading.value = true // Set loading to true when operation starts

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false // Set loading to false when operation ends

                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        saveUserToFirestore(it.uid, email, username)
                    }
                    _user.value = auth.currentUser
                    _authStatus.value = "Registration successful!"
                } else {
                    _authStatus.value = "Registration failed: ${task.exception?.message}"
                }
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, username: String) {
        val userMap = hashMapOf(
            "uid" to uid,
            "email" to email,
            "username" to username
        )
        firestore.collection("users").document(uid).set(userMap)
            .addOnSuccessListener {
                _username.value = username
            }
            .addOnFailureListener {
                _authStatus.value = "Error saving user: ${it.message}"
            }
    }

    fun login(email: String, password: String) {
        _isLoading.value = true // Set loading to true when operation starts

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isLoading.value = false // Set loading to false when operation ends

                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _user.value?.let { fetchUsername(it.uid) } // Fetch username after login
                    _authStatus.value = "Login successful!"
                } else {
                    _authStatus.value = "Login failed: ${task.exception?.message}"
                }
            }
    }

    private fun fetchUsername(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _username.value = document.getString("username")
                }
            }
            .addOnFailureListener {
                _authStatus.value = "Failed to fetch username"
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _username.value = null
        _authStatus.value = "User logged out!"
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authStatus.value = "Password reset email sent!"
                } else {
                    _authStatus.value = "Failed to send reset email: ${task.exception?.message}"
                }
            }
    }

    fun deleteUser() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = null
                _authStatus.value = "User deleted successfully!"
            } else {
                _authStatus.value = "Failed to delete user: ${task.exception?.message}"
            }
        }
    }
}