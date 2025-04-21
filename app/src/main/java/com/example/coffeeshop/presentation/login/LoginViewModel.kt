package com.example.coffeeshop.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// presentation/login/LoginViewModel.kt
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context // Hilt ile Context sağlama
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // SharedPreferences
    private val sharedPrefs = context.getSharedPreferences("coffee_prefs", Context.MODE_PRIVATE)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    saveUserToPrefs(user.userId)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Geçersiz email veya şifre!")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Hata: ${e.message}")
            }
        }
    }

    private fun saveUserToPrefs(userId: String) {
        sharedPrefs.edit().putString("userId", userId).apply()
    }

    fun checkAutoLogin(): Boolean {
        return sharedPrefs.getString("userId", null) != null
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}