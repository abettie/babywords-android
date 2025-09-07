package work.makedara.babywords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

data class UserInfo(
    val uid: String,
    val displayName: String?,
    val email: String?
)

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: UserInfo) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                _authState.value = AuthState.Authenticated(mapFirebaseUserToUserInfo(firebaseUser))
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun signInWithGoogleCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val userInfo = mapFirebaseUserToUserInfo(firebaseUser)
                    // Log user information
                    println("User UID: ${userInfo.uid}")
                    println("User Name: ${userInfo.displayName}")
                    println("User Email: ${userInfo.email}")
                    _authState.value = AuthState.Authenticated(userInfo)
                } else {
                    _authState.value = AuthState.Error("Firebase authentication failed.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun mapFirebaseUserToUserInfo(firebaseUser: FirebaseUser): UserInfo {
        return UserInfo(
            uid = firebaseUser.uid,
            displayName = firebaseUser.displayName,
            email = firebaseUser.email
        )
    }
}