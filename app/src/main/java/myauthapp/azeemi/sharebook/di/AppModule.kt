package myauthapp.azeemi.sharebook.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import myauthapp.azeemi.sharebook.data.repository.FirebaseAuthRepository
import myauthapp.azeemi.sharebook.data.repository.FirebaseBookRepository
import myauthapp.azeemi.sharebook.domain.repository.AuthRepository
import myauthapp.azeemi.sharebook.domain.repository.BookRepository
import myauthapp.azeemi.sharebook.domain.usecase.AuthUseCase
import myauthapp.azeemi.sharebook.domain.usecase.BookUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): AuthRepository = FirebaseAuthRepository(auth, firestore, storage)

    @Provides
    @Singleton
    fun provideAuthUseCase(
        authRepository: AuthRepository
    ): AuthUseCase = AuthUseCase(authRepository)
    
    @Provides
    @Singleton
    fun provideBookRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        @ApplicationContext context: Context
    ): BookRepository = FirebaseBookRepository(firestore, storage, context)
    
    @Provides
    @Singleton
    fun provideBookUseCase(
        bookRepository: BookRepository
    ): BookUseCase = BookUseCase(bookRepository)
}