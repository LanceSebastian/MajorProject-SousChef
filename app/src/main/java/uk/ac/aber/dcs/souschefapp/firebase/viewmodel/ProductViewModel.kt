package uk.ac.aber.dcs.souschefapp.firebase.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.firebase.EditMode
import uk.ac.aber.dcs.souschefapp.firebase.ImageRepository
import uk.ac.aber.dcs.souschefapp.firebase.Product
import uk.ac.aber.dcs.souschefapp.firebase.ProductRepository
import uk.ac.aber.dcs.souschefapp.firebase.UploadState

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val imageRepository = ImageRepository()

    private val _uploadState = MutableLiveData<UploadState>(UploadState.Idle)
    val uploadState: LiveData<UploadState> = _uploadState

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

    private fun resetUploadStateAfterDelay(delayMillis: Long = 3000L) {
        viewModelScope.launch {
            delay(delayMillis)
            _uploadState.value = UploadState.Idle
        }
    }

    fun setMode(newEditMode: EditMode){
        _Edit_mode.value = newEditMode
    }

    fun createProduct(userId: String?, product: Product, imageUri: Uri? = null, context: Context){
        if (userId == null) return


        _uploadState.value = UploadState.Loading
        viewModelScope.launch {
            val imageUrl = try {
                imageUri?.let { imageRepository.uploadImage(it) }
            } catch (e: Exception) {
                android.util.Log.e("ProductViewModel", "Image upload failed: ${e.message}")
                null // fall back to no image
            }

            val standardProduct = product.copy(
                imageUrl = imageUrl,
                createdBy = userId
            )
            val savedProduct = productRepository.addProduct(userId, standardProduct)

            if (savedProduct != null) {
                _selectProduct.postValue(savedProduct)
                _uploadState.value = UploadState.Success(imageUrl ?: "")
                resetUploadStateAfterDelay()
                Toast.makeText(context, "Product saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                _uploadState.value = UploadState.Error(imageUrl ?: "")
                resetUploadStateAfterDelay()
                Toast.makeText(context, "Failed to save product.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun createProductAndId(userId: String?, product: Product? = null, imageUri: Uri? = null, context: Context): String? {
        if (userId == null) {
            android.util.Log.e("ProductViewModel", "Failed to create product due to null userId")
            return null
        }

        _uploadState.value = UploadState.Loading
        val imageUrl = try {
            imageUri?.let { imageRepository.uploadImage(it) }
        } catch (e: Exception) {
            android.util.Log.e("ProductViewModel", "Image upload failed: ${e.message}")
            null // fall back to no image
        }

        val standardProduct = product?.copy(
            imageUrl = imageUrl,
            createdBy = userId
        ) ?: Product(
            name = "Unnamed",
            imageUrl = imageUrl,
            createdBy = userId
        )

        val savedProduct = productRepository.addProduct(userId, standardProduct)

        return if (savedProduct != null) {
            //Toast.makeText(context, "Product saved successfully!", Toast.LENGTH_SHORT).show()
            _uploadState.value = UploadState.Success(imageUrl ?: "")
            resetUploadStateAfterDelay()
            savedProduct.productId
        } else {
            //Toast.makeText(context, "Failed to save product.", Toast.LENGTH_SHORT).show()
            _uploadState.value = UploadState.Error(imageUrl ?: "")
            resetUploadStateAfterDelay()
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

    fun updateProduct(userId: String?, product: Product, imageUri: Uri? =  null, context: Context){
        if (userId == null) return

        _uploadState.value = UploadState.Loading


        viewModelScope.launch {
            val imageUrl = try {
                imageUri?.let { imageRepository.uploadImage(it) }
            } catch (e: Exception) {
                android.util.Log.e("ProductViewModel", "Image upload failed: ${e.message}")
                _uploadState.value = UploadState.Error("Image upload failed")
                resetUploadStateAfterDelay()
                null // fall back to no image
            }

            val finalImageUrl = imageUrl ?: product.imageUrl
            val standardProduct = product.copy(imageUrl = finalImageUrl)
            val isSuccess = productRepository.updateProduct(userId, standardProduct)

            if (isSuccess) {
                _uploadState.value = UploadState.Success("Product updated successfully")
            } else {
                _uploadState.value = UploadState.Error("Failed to update product")
            }

            resetUploadStateAfterDelay()

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

    fun deleteProduct(userId: String?, product: Product, context: Context){
        if (userId == null) return

        viewModelScope.launch {
            val isSuccess = productRepository.deleteProduct(
                userId = userId,
                product = product,
            )
            val message = if (isSuccess) "Product deleted successfully!" else "Failed to delete product."
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}