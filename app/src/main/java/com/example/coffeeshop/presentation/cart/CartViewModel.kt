package com.example.coffeeshop.presentation.cart

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.MyApplication
import com.example.coffeeshop.data.local.SharedPrefsHelper
import com.example.coffeeshop.data.local.entity.Product
import com.example.coffeeshop.data.repository.ProductRepository
import com.example.coffeeshop.data.repository.UserRepository
import com.example.coffeeshop.presentation.home.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Cart UI state
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()
    var totalAmount = 0.0

    // Message to display (snackbar/toast)
    var message by mutableStateOf<String?>(null)
        private set

    init {
        loadCartItems()
    }

    // Load products from cart product IDs list
    private fun loadCartItems() {
        viewModelScope.launch {
            try {
                if (HomeViewModel.cartProductIds.isEmpty()) {
                    _uiState.update { it.copy(isLoading = false, isEmpty = true) }
                    return@launch
                }

                // Group product IDs by counting occurrences (for quantities)
                val productCountMap = mutableMapOf<Long, Int>()
                HomeViewModel.cartProductIds.forEach { productId ->
                    productCountMap[productId] = productCountMap.getOrDefault(productId, 0) + 1
                }

                val cartItems = mutableListOf<CartItem>()
                totalAmount = 0.0

                // Load each unique product with its count
                productCountMap.forEach { (productId, count) ->
                    productRepository.getById(productId)?.let { product ->
                        val cartItem = CartItem(product, count)
                        cartItems.add(cartItem)
                        totalAmount += product.price * count
                    }
                }

                _uiState.update {
                    it.copy(
                        cartItems = cartItems,
                        totalAmount = totalAmount,
                        isLoading = false,
                        isEmpty = cartItems.isEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading cart items"
                    )
                }
            }
        }
    }


    // Increase item quantity
    fun increaseQuantity(productId: Long) {
        // Add another instance to HomeViewModel.cartProductIds
        HomeViewModel.cartProductIds.add(productId)

        // Update UI state
        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.map { cartItem ->
            if (cartItem.product.productId == productId) {
                // Increment quantity
                cartItem.copy(quantity = cartItem.quantity + 1)
            } else {
                cartItem
            }
        }

        // Recalculate total amount
        val newTotalAmount = calculateTotalAmount(updatedItems)

        _uiState.update {
            it.copy(
                cartItems = updatedItems,
                totalAmount = newTotalAmount
            )
        }
    }

    // Decrease item quantity
    fun decreaseQuantity(productId: Long) {
        // Remove one instance from HomeViewModel.cartProductIds
        val index = HomeViewModel.cartProductIds.indexOf(productId)
        if (index != -1) {
            HomeViewModel.cartProductIds.removeAt(index)
        }

        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.map { cartItem ->
            if (cartItem.product.productId == productId && cartItem.quantity > 1) {
                // Decrement quantity if more than 1
                cartItem.copy(quantity = cartItem.quantity - 1)
            } else {
                cartItem
            }
        }

        // Recalculate total amount
        val newTotalAmount = calculateTotalAmount(updatedItems)

        _uiState.update {
            it.copy(
                cartItems = updatedItems,
                totalAmount = newTotalAmount
            )
        }
    }

    // Remove item from cart
    fun removeFromCart(productId: Long) {
        val currentState = _uiState.value
        val updatedItems = currentState.cartItems.filter {
            it.product.productId != productId
        }

        // Remove from HomeViewModel's static list
        HomeViewModel.cartProductIds.remove(productId)

        // Recalculate total amount
        val newTotalAmount = calculateTotalAmount(updatedItems)

        _uiState.update {
            it.copy(
                cartItems = updatedItems,
                totalAmount = newTotalAmount,
                isEmpty = updatedItems.isEmpty()
            )
        }
    }

    // Process checkout
    fun checkout() {
        val sharedPrefs = SharedPrefsHelper(MyApplication.getContext())


        sendOrder(
            email =  sharedPrefs.getUserId()!!,
            totalAmount = totalAmount,
            onSuccess = {
                // Başarılıysa UI güncellemesi vs.
            },
            onError = { err ->
                // Hata mesajı göster

            }
        )

        // Clear cart
        HomeViewModel.cartProductIds.clear()

        // Reset UI state
        _uiState.update {
            it.copy(
                cartItems = emptyList(),
                totalAmount = 0.0,
                isEmpty = true
            )
        }

        // Show success message
        message = "Satın alma işlemi başarıyla gerçekleştirildi."
    }

    // Calculate total based on current items and quantities
    private fun calculateTotalAmount(items: List<CartItem>): Double {
        return items.sumOf { it.product.price * it.quantity }
    }

    // Clear message after displaying
    fun clearMessage() {
        message = null
    }

    // Data classes for state management
    data class CartUiState(
        val cartItems: List<CartItem> = emptyList(),
        val totalAmount: Double = 0.0,
        val isLoading: Boolean = true,
        val isEmpty: Boolean = false,
        val error: String? = null
    )

    data class CartItem(
        val product: Product,
        val quantity: Int
    )

    fun sendOrder(
        email: String,
        totalAmount: Double,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {

        val client = OkHttpClient()
        // JSON objesini oluştur
        val json = JSONObject().apply {
            put("email", email)
            put("productIds", JSONArray(HomeViewModel.cartProductIds))
            put("totalAmount", totalAmount)
        }

        // RequestBody hazırla
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        // POST isteğini hazırla (URL’i kendi API’n ile değiştir)
        val request = Request.Builder()
            .url("https://webhook.site/3ae00c13-668e-46db-a524-ee3cc18f25f9")
            .post(body)
            .build()

        // Asenkron çağrı
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(IOException("HTTP ${response.code}: ${response.message}"))
                }
                response.close()
            }
        })
    }
}

