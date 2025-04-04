package uk.ac.aber.dcs.souschefapp.room_viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.souschefapp.database.SousChefRepository
import uk.ac.aber.dcs.souschefapp.database.models.Product

/*      Handles Product Manipulation       */
class ProductViewModel(
    application: Application
) : AndroidViewModel(application) {
    val repository: SousChefRepository = SousChefRepository(application)

    fun insertProduct(product: Product){
        viewModelScope.launch(Dispatchers.IO){
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: Product){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteProduct(product)
        }
    }

    fun deleteProduct(productId: Int){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteProduct(productId)
        }
    }

    fun getAllProductsFromAccount(accountId: Int): LiveData<List<Product>>{
        return repository.getAllProductsFromAccount(accountId)
    }

    fun getProductFromId(productId: Int): LiveData<Product>{
        return repository.getProductFromId(productId)
    }

}