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
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.ProductRepository

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _Edit_mode = MutableLiveData(EditMode.View)
    val editMode: LiveData<EditMode> = _Edit_mode

    private var productListener: ListenerRegistration? = null

    private var _userProducts = MutableLiveData<List<Product>>()
    var userProducts: LiveData<List<Product>> = _userProducts

    private var _logProducts = MutableLiveData<List<Product>>()
    var logProducts: LiveData<List<Product>> = _userProducts


    private var selectProductId: String? = null
    private var _selectProduct = MediatorLiveData<Product?>()
    var selectProduct: LiveData<Product?> = _selectProduct

    init {
        _selectProduct.addSource(_userProducts) { products ->
            selectProductId?.let { id ->
                _selectProduct.value = products.find { it.productId == id }
            }
        }
    }

    fun setMode(newEditMode: EditMode){
        _Edit_mode.value = newEditMode
    }

    fun createProduct(userId: String?, product: Product, context: Context){
        if (userId == null) return

        val standardProduct = product.copy(createdBy = userId)

        viewModelScope.launch {
            val savedProduct = productRepository.addProduct(userId, standardProduct)

            if (savedProduct != null) {
                _selectProduct.postValue(savedProduct)
                Toast.makeText(context, "Product saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save product.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun createProductAndId(userId: String?, product: Product? = null, context: Context): String? {
        if (userId == null) {
            android.util.Log.e("ProductViewModel", "Failed to create product due to null userId")
            return null
        }

        val standardProduct = product?.copy(
            createdBy = userId
        ) ?: Product(
            name = "Unnamed",
            createdBy = userId
        )
        val savedProduct = productRepository.addProduct(userId, standardProduct)

        return if (savedProduct != null) {
            //Toast.makeText(context, "Product saved successfully!", Toast.LENGTH_SHORT).show()
            savedProduct.productId
        } else {
            //Toast.makeText(context, "Failed to save product.", Toast.LENGTH_SHORT).show()
            null
        }

    }

    fun readProducts(userId: String?){
        if (userId == null) return

        _isLoading.postValue(true)

        productListener?.remove() // Stop previous listener if it exists

        productListener = productRepository.listenForProducts(userId) { products ->
            _userProducts.postValue(products)
            _isLoading.postValue(false)
        }
    }

    fun selectProduct(productId: String){
        selectProductId = productId
        _userProducts.value?.let { products ->
            _selectProduct.value = products.find { it.productId == productId }
        }
    }

    fun clearSelectProduct(){
        selectProductId = null
        _selectProduct.value = null
    }

    fun getProductsFromList(userId: String?, productIdList: List<String>){
        if (userId == null) return

        viewModelScope.launch {
            val products = mutableListOf<Product>()

            productIdList.forEach { productId ->
                try {
                    val product = productRepository.findProductById(userId, productId)
                    products.add(product)
                } catch (e: Exception) {
                    android.util.Log.e("Firestore", "Failed to fetch product $productId", e)
                }
            }

            _logProducts.postValue(products)
        }
    }

    fun stopListening(){
        productListener?.remove()
        productListener = null
    }

    fun updateProduct(userId: String?, product: Product, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.updateProduct(userId, product)

            val message = if (isSuccess) "Product saved successfully!" else "Failed to save product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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

    fun deleteProduct(userId: String?, productId: String, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.deleteProduct(
                userId = userId,
                productId = productId,
            )
            val message = if (isSuccess) "Product deleted successfully!" else "Failed to delete product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}