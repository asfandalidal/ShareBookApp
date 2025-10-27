package myauthapp.azeemi.sharebook.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import myauthapp.azeemi.sharebook.domain.model.AuthUser
import myauthapp.azeemi.sharebook.domain.model.User
import myauthapp.azeemi.sharebook.domain.usecase.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(authUseCase.isLoggedIn)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(authUseCase.currentUser)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authUseCase.signIn(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    loadUserData()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Sign in failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, userData: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authUseCase.signUp(email, password, userData)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    _userData.value = userData
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Sign up failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            Log.d("AuthViewModel", "Sign out called")
            _isLoading.value = true
            
            authUseCase.signOut()
                .onSuccess {
                    Log.d("AuthViewModel", "Sign out successful")
                    _currentUser.value = null
                    _isLoggedIn.value = false
                    _userData.value = null
                }
                .onFailure { exception ->
                    Log.e("AuthViewModel", "Sign out failed: ${exception.message}")
                    _errorMessage.value = exception.message ?: "Sign out failed"
                }
            
            _isLoading.value = false
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            authUseCase.getCurrentUserData()
                .onSuccess { user ->
                    _userData.value = user
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Failed to load user data"
                }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authUseCase.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _currentUser.value = user
                    _isLoggedIn.value = true
                    loadUserData()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message ?: "Google sign in failed"
                }
            
            _isLoading.value = false
        }
    }

    fun updateUserProfile(user: User, profileImageUri: Uri? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = authUseCase.updateUserProfile(user, profileImageUri)
            if (result.isSuccess) {
                // Refresh user data
                loadUserData()
                _errorMessage.value = "Profile updated successfully!"
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update profile"
            }

            _isLoading.value = false
        }
    }
}