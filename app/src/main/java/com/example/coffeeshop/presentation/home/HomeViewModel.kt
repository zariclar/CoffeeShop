package com.example.coffeeshop.presentation.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.local.entity.Category
import com.example.coffeeshop.data.local.entity.Product
import com.example.coffeeshop.data.repository.CategoryRepository
import com.example.coffeeshop.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // State for the home screen
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Static cart items list to hold product IDs that are added to cart
    companion object {
        val cartProductIds = mutableListOf<Long>()
    }

    // Initialize ViewModel
    init {


        loadCategories()
        loadProducts()
    }

    // Load all categories
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = categoryRepository.listAll()
                _uiState.update { currentState ->
                    currentState.copy(
                        categories = categories,
                        selectedCategoryId = -1
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = "Failed to load categories: ${e.message}")
                }
            }
        }
    }

    // Load all products
    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = productRepository.listAll()
                _uiState.update { currentState ->
                    currentState.copy(
                        allProducts = products,
                        filteredProducts = filterProducts(
                            products,
                            currentState.selectedCategoryId,
                            currentState.searchQuery
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = "Failed to load products: ${e.message}")
                }
            }
        }
    }

    // Filter products based on selected category and search query
    private fun filterProducts(
        products: List<Product>,
        categoryId: Long,
        query: String
    ): List<Product> {
        return products.filter { product ->
            val matchesCategory = categoryId == -1L || product.categoryId == categoryId
            val matchesQuery = query.isEmpty() || product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    // Select a category
    fun selectCategory(categoryId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryId = categoryId,
                filteredProducts = filterProducts(
                    currentState.allProducts,
                    categoryId,
                    currentState.searchQuery
                )
            )
        }
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredProducts = filterProducts(
                    currentState.allProducts,
                    currentState.selectedCategoryId,
                    query
                )
            )
        }
    }

    // Add product to cart
    fun addToCart(productId: Long) {
        cartProductIds.add(productId)
        // Show success toast
        Toast.makeText(
            context,
            "Ürün başarıyla sepete eklendi",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Optional: Method to get product details for a specific product (for detail screen)
    fun getProductById(productId: Long) {
        viewModelScope.launch {
            try {
                val product = productRepository.getById(productId)
                // You can handle this data as needed for navigation to detail screen
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = "Failed to get product details: ${e.message}")
                }
            }
        }
    }
}

// State holder for Home screen
data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategoryId: Long = -1L,
    val searchQuery: String = "",
    val error: String? = null
)