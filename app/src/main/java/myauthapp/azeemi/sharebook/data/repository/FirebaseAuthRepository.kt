package myauthapp.azeemi.sharebook.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import myauthapp.azeemi.sharebook.domain.model.AuthUser
import myauthapp.azeemi.sharebook.domain.model.User
import myauthapp.azeemi.sharebook.domain.repository.AuthRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {
    
    private val _currentUserFlow = MutableStateFlow<AuthUser?>(null)
    val currentUserFlow: StateFlow<AuthUser?> = _currentUserFlow.asStateFlow()

    init {
        // Initialize current user on creation
        val current = auth.currentUser
        if (current != null) {
            _currentUserFlow.value = toAuthUser(current)
        }
        
        // Listen to auth state changes
        auth.addAuthStateListener { _ ->
            val currentUser = auth.currentUser
            _currentUserFlow.value = currentUser?.let { toAuthUser(it) }
            Log.d("FirebaseAuth", "Auth state changed: ${currentUser?.uid}")
        }
    }

    override val currentUser: AuthUser?
        get() {
            val user = auth.currentUser
            return if (user != null) toAuthUser(user) else null
        }

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun signIn(email: String, password: String): Result<AuthUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val authUser = toAuthUser(firebaseUser)
                Result.success(authUser)
            } else {
                Result.failure(Exception("Sign in failed: User is null"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign in failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, userData: User): Result<AuthUser> {
        return try {
            // Create user with email and password
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            
            if (firebaseUser == null) {
                return Result.failure(Exception("Sign up failed: User is null"))
            }
            
            // Create user document in Firestore
            val userDataWithId = userData.copy(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email
            )
            
            // Update Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(userDataWithId)
                .await()
            
            val authUser = toAuthUser(firebaseUser)
            Result.success(authUser)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign up failed", e)
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Log.d("FirebaseAuth", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign out failed", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserData(): Result<User> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                return Result.failure(Exception("No user logged in"))
            }
            
            val document = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()
            
            if (!document.exists()) {
                // Create default user data
                val defaultUser = User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = "",
                    location = "",
                    bio = null
                )
                // Save to Firestore
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(defaultUser)
                    .await()
                Result.success(defaultUser)
            } else {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to parse user data"))
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Get current user data failed", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserData(user: User): Result<Unit> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                return Result.failure(Exception("No user logged in"))
            }
            
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()
            
            Log.d("FirebaseAuth", "User data updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Update user data failed", e)
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User, profileImageUri: Uri?): Result<Unit> {
        return try {
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                return Result.failure(Exception("No user logged in"))
            }
            
            // Upload profile image if provided
            if (profileImageUri != null) {
                val imageUrl = uploadProfileImage(firebaseUser.uid, profileImageUri)
                val userWithImage = user.copy(profileImageUrl = imageUrl)
                
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .set(userWithImage)
                    .await()
            } else {
                updateUserData(user)
            }
            
            Log.d("FirebaseAuth", "User profile updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Update user profile failed", e)
            Result.failure(e)
        }
    }
    
    // Google Sign-In method
    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user
            
            if (firebaseUser == null) {
                return Result.failure(Exception("Google sign in failed: User is null"))
            }
            
            // Check if user exists in Firestore
            val userData = getCurrentUserData()
            if (userData.isFailure) {
                // User doesn't exist, create user data
                val newUser = User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    phone = "",
                    location = "",
                    bio = null,
                    profileImageUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                updateUserData(newUser)
            }
            
            val authUser = toAuthUser(firebaseUser)
            Result.success(authUser)
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Google sign in failed", e)
            Result.failure(e)
        }
    }
    
    private suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        return try {
            val fileName = "profile_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child("profile_images/$fileName")
            
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Upload profile image failed", e)
            throw e
        }
    }
    
    private fun toAuthUser(firebaseUser: FirebaseUser): AuthUser {
        return AuthUser(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isEmailVerified = firebaseUser.isEmailVerified
        )
    }
}

// Extension function to convert User to HashMap for Firestore
fun User.toFirestoreMap(): HashMap<String, Any?> {
    return hashMapOf(
        "uid" to uid,
        "name" to name,
        "email" to email,
        "phone" to phone,
        "location" to location,
        "bio" to bio,
        "profileImageUrl" to profileImageUrl,
        "localProfileImagePath" to localProfileImagePath,
        "createdAt" to createdAt,
        "updatedAt" to updatedAt
    )
}

// Extension function to convert Firestore document to User
fun DocumentSnapshot.toUser(): User? {
    return try {
        User(
            uid = getString("uid") ?: "",
            name = getString("name") ?: "",
            email = getString("email") ?: "",
            phone = getString("phone") ?: "",
            location = getString("location") ?: "",
            bio = getString("bio"),
            profileImageUrl = getString("profileImageUrl") ?: "",
            localProfileImagePath = getString("localProfileImagePath") ?: "",
            createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}
