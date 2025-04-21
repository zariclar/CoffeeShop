package com.example.coffeeshop.presentation.register
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeshop.data.local.entity.User
import com.example.coffeeshop.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(name: String, email: String, password: String) {
        // Boş Alan Kontrolü
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _registerState.value = RegisterState.Error("Lütfen tüm alanları doldurun!")
            return
        }

        // Email Format Kontrolü
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _registerState.value = RegisterState.Error("Geçersiz email formatı!")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // Email'in Zaten Kayıtlı Olup Olmadığını Kontrol Et
                val existingUser = repository.getUserByEmail(email)
                if (existingUser != null) {
                    _registerState.value = RegisterState.Error("Bu email zaten kayıtlı!")
                    return@launch
                }

                // Kayıt İşlemi
                val hashedPassword = password
                val user = User(userId = email, name = name, password = hashedPassword)
                repository.register(user)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Kayıt başarısız: ${e.message}")
            }
        }
    }



    sealed class RegisterState {
        object Idle : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
}