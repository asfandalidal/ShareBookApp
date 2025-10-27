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
import myauthapp.azeemi.sharebook.domain.model.User
import myauthapp.azeemi.sharebook.domain.usecase.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        Log.d("ProfileViewModel", "Initializing ProfileViewModel")
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Loading user data...")
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Check if user is logged in first
                val authUser = authUseCase.currentUser
                if (authUser == null) {
                    Log.e("ProfileViewModel", "No user logged in")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No user logged in. Please sign in again."
                    )
                    return@launch
                }
                
                Log.d("ProfileViewModel", "User found: ${authUser.uid}")
                
                val result = authUseCase.getCurrentUserData()
                result.onSuccess { user ->
                    Log.d("ProfileViewModel", "User data loaded successfully: ${user.name}")
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isLoading = false,
                        name = user.name,
                        email = user.email,
                        phone = user.phone,
                        location = user.location,
                        bio = user.bio ?: ""
                    )
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to load user data: ${exception.message}")
                    
                    // If user document doesn't exist, create a basic one
                    if (exception.message?.contains("User data not found") == true) {
                        Log.d("ProfileViewModel", "Creating new user document...")
                        val newUser = User(
                            uid = authUser.uid,
                            name = authUser.displayName ?: "",
                            email = authUser.email ?: "",
                            phone = "",
                            location = "",
                            bio = "",
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        
                        // Save the new user document
                        authUseCase.updateUserData(newUser)
                            .onSuccess {
                                Log.d("ProfileViewModel", "New user document created")
                                _uiState.value = _uiState.value.copy(
                                    user = newUser,
                                    isLoading = false,
                                    name = newUser.name,
                                    email = newUser.email,
                                    phone = newUser.phone,
                                    location = newUser.location,
                                    bio = newUser.bio ?: ""
                                )
                            }
                            .onFailure { createException ->
                                Log.e("ProfileViewModel", "Failed to create user document: ${createException.message}")
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Failed to create user profile: ${createException.message}"
                                )
                            }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load user data"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception in loadUserData: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Exception: ${e.message}"
                )
            }
        }
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(location = location)
    }

    fun updateBio(bio: String) {
        _uiState.value = _uiState.value.copy(bio = bio)
    }

    fun setSelectedImageUri(uri: Uri?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Uploading profile image...")
            _uiState.value = _uiState.value.copy(
                isUploadingImage = true,
                errorMessage = null
            )

            val currentUser = _uiState.value.user
            if (currentUser != null) {
                try {
                    Log.d("ProfileViewModel", "Updating profile with image for user: ${currentUser.uid}")
                    updateUserProfile(currentUser, imageUri)
                } catch (e: Exception) {
                    Log.e("ProfileViewModel", "Exception in uploadProfileImage: ${e.message}")
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        errorMessage = "Image upload failed: ${e.message}"
                    )
                }
            } else {
                Log.e("ProfileViewModel", "User not found for image upload")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    errorMessage = "User not found"
                )
            }
        }
    }

    fun updateUserProfile(user: User, profileImageUri: Uri? = null) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Updating user profile...")
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val result = authUseCase.updateUserProfile(user, profileImageUri)
                result.onSuccess {
                    Log.d("ProfileViewModel", "Profile updated successfully")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isUploadingImage = false,
                        successMessage = "Profile updated successfully!",
                        user = user
                    )
                }.onFailure { exception ->
                    Log.e("ProfileViewModel", "Failed to update profile: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isUploadingImage = false,
                        errorMessage = exception.message ?: "Failed to update profile"
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Exception in updateUserProfile: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isUploadingImage = false,
                    errorMessage = "Exception: ${e.message}"
                )
            }
        }
    }

    fun saveProfileChanges() {
        Log.d("ProfileViewModel", "Saving profile changes...")
        val currentState = _uiState.value
        val currentUser = currentState.user
        
        if (currentUser == null) {
            Log.e("ProfileViewModel", "Cannot save changes: user is null")
            
            // Try to reload user data first
            Log.d("ProfileViewModel", "Attempting to reload user data...")
            loadUserData()
            
            _uiState.value = _uiState.value.copy(
                errorMessage = "User data not loaded. Please try again."
            )
            return
        }

        Log.d("ProfileViewModel", "Current user: ${currentUser.name}")
        Log.d("ProfileViewModel", "Updated name: ${currentState.name}")
        Log.d("ProfileViewModel", "Updated email: ${currentState.email}")

        val updatedUser = currentUser.copy(
            name = currentState.name,
            email = currentState.email,
            phone = currentState.phone,
            location = currentState.location,
            bio = currentState.bio,
            updatedAt = System.currentTimeMillis()
        )

        updateUserProfile(updatedUser, currentState.selectedImageUri)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun setEditingMode(isEditing: Boolean) {
        _uiState.value = _uiState.value.copy(isEditing = isEditing)
    }

    fun cancelEditing() {
        val currentUser = _uiState.value.user
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                isEditing = false,
                name = currentUser.name,
                email = currentUser.email,
                phone = currentUser.phone,
                location = currentUser.location,
                bio = currentUser.bio ?: "",
                selectedImageUri = null,
                errorMessage = null
            )
        }
    }
}

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val isEditing: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val location: String = "",
    val bio: String = "",
    val selectedImageUri: Uri? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)