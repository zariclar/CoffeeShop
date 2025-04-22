package com.example.coffeeshop.presentation.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.local.entity.Category
import com.example.coffeeshop.data.local.entity.Favorite
import com.example.coffeeshop.data.local.entity.Product
import com.example.coffeeshop.data.repository.CategoryRepository
import com.example.coffeeshop.data.repository.FavoriteRepository
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
    private val favoriteRepository: FavoriteRepository, // Add favorite repository
    @ApplicationContext private val context: Context
) : ViewModel() {

    // State for the home screen
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Static cart items list to hold product IDs that are added to cart
    companion object {
        val cartProductIds = mutableListOf<Long>()

        // For demo purpose, using a static user ID (in real app, this would come from user authentication)
        const val CURRENT_USER_ID = "mustafa@zariclar.com"
    }

    // Initialize ViewModel
    init {
        loadCategories()
        loadProducts()
        loadFavorites()
    }

    // Load favorite products for the current user
    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = favoriteRepository.list(CURRENT_USER_ID.toString())
                val favoriteProductIds = favorites.map { it.productId }.toSet()

                _uiState.update { currentState ->
                    currentState.copy(favoriteProductIds = favoriteProductIds)
                }

                // Re-filter products to include favorites information
                updateFilteredProducts()
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(error = "Failed to load favorites: ${e.message}")
                }
            }
        }
    }

    // Load all categories
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = categoryRepository.listAll()
                _uiState.update { currentState ->
                    currentState.copy(
                        categories = categories,
                        selectedCategoryId = -1L
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
                            currentState.searchQuery,
                            currentState.favoriteProductIds
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

    // Filter products based on selected category, search query, and favorites
    private fun filterProducts(
        products: List<Product>,
        categoryId: Long,
        query: String,
        favoriteProductIds: Set<Long>
    ): List<Product> {
        return products.filter { product ->
            val matchesCategory = when {
                categoryId == -1L -> true // All products
                categoryId == -2L -> favoriteProductIds.contains(product.productId) // Favorites
                else -> product.categoryId == categoryId // Specific category
            }
            val matchesQuery = query.isEmpty() || product.name.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    // Update filtered products when any filter criteria changes
    private fun updateFilteredProducts() {
        _uiState.update { currentState ->
            currentState.copy(
                filteredProducts = filterProducts(
                    currentState.allProducts,
                    currentState.selectedCategoryId,
                    currentState.searchQuery,
                    currentState.favoriteProductIds
                )
            )
        }
    }

    // Select a category
    fun selectCategory(categoryId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedCategoryId = categoryId
            )
        }
        updateFilteredProducts()
    }

    // Update search query
    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query
            )
        }
        updateFilteredProducts()
    }

    // Toggle favorite status
    fun toggleFavorite(productId: Long) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val isFavorite = currentState.favoriteProductIds.contains(productId)

                if (isFavorite) {
                    // Find the favorite to remove
                    val favorites = favoriteRepository.list(CURRENT_USER_ID.toString())
                    val favoriteToRemove = favorites.find { it.productId == productId }
                    favoriteToRemove?.let {
                        favoriteRepository.remove(it)
                        // Update UI state
                        _uiState.update { state ->
                            state.copy(
                                favoriteProductIds = state.favoriteProductIds - productId
                            )
                        }
                        Toast.makeText(
                            context,
                            "Favorilerden çıkarıldı",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Add to favorites
                    favoriteRepository.add(CURRENT_USER_ID, productId)
                    // Update UI state
                    _uiState.update { state ->
                        state.copy(
                            favoriteProductIds = state.favoriteProductIds + productId
                        )
                    }
                    Toast.makeText(
                        context,
                        "Favorilere eklendi",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Refresh filtered products if we're in favorites view
                if (currentState.selectedCategoryId == -2L) {
                    updateFilteredProducts()
                }

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(error = "Favorileri güncellerken hata oluştu: ${e.message}")
                }
                Toast.makeText(
                    context,
                    "İşlem sırasında bir hata oluştu",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

// Updated State holder for Home screen
data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedCategoryId: Long = -1L,
    val searchQuery: String = "",
    val favoriteProductIds: Set<Long> = emptySet(), // Added favoriteProductIds
    val error: String? = null
)