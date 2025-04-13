package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.ProductRepository
import uk.ac.aber.dcs.souschefapp.firebase.Recipe

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private var productListener: ListenerRegistration? = null
    private var _userProducts = MutableLiveData<List<Product>>()
    var userProducts: LiveData<List<Product>> = _userProducts


    private var selectProductId: String? = null
    private var _selectProduct = MediatorLiveData<Product>()
    var selectRecipe: LiveData<Product> = _selectProduct

    init {
        _selectProduct.addSource(_userProducts) { products ->
            selectProductId?.let { id ->
                _selectProduct.value = products.find { it.productId == id }
            }
        }
    }

    fun createProduct(userId: String?, product: Product){
        if (userId == null) return

        viewModelScope.launch{
            val isSuccess = productRepository.addProduct(userId, product)

            if (!isSuccess) {
                android.util.Log.e("ProductViewModel", "Failed to create product")
            }
        }
    }

    fun readProduct(userId: String?){
        if (userId == null) return

        productListener?.remove() // Stop previous listener if it exists

        productListener = productRepository.listenForProducts(userId) { products ->
            _userProducts.postValue(products)
        }
    }

    fun selectProduct(productId: String){
        selectProductId = productId
        _userProducts.value?.let { products ->
            _selectProduct.value = products.find { it.productId == productId }
        }
    }

    fun stopListening(){
        productListener?.remove()
        productListener = null
    }

    fun updateProduct(userId: String?, product: Product){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.updateProduct(userId, product)

            if (!isSuccess) {
                android.util.Log.e("ProductViewModel", "Failed to update product")
            }
        }
    }

    fun archiveProduct(userId: String?, productId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.archiveProduct(
                userId = userId,
                productId = productId,
            )
            val message = if (isSuccess) "Product archived successfully!" else "Failed to archive product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreProduct(userId: String?, productId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.restoreProduct(
                userId = userId,
                productId = productId,
            )
            val message = if (isSuccess) "Product restored successfully!" else "Failed to restore product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteProduct(userId: String?, productId: String){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.deleteProduct(
                userId = userId,
                productId = productId,
            )
            val message = if (isSuccess) "Product restored successfully!" else "Failed to restore product."
            android.util.Log.e("ProductViewModel", message)
        }
    }
}