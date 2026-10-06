package com.example.data.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

sealed interface AuthResult {
    data class Success(val user: FirebaseUser) : AuthResult
    data class Error(val message: String) : AuthResult
}

class FirebaseAuthService(private val context: Context) {

    private fun getAuth(): FirebaseAuth? {
        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private val _currentUserState = MutableStateFlow<FirebaseUser?>(getAuth()?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUserState.asStateFlow()

    init {
        getAuth()?.addAuthStateListener { auth ->
            _currentUserState.value = auth.currentUser
        }
    }

    fun isUserSignedIn(): Boolean = getAuth()?.currentUser != null

    suspend fun signInWithEmail(email: String, pass: String): AuthResult {
        val auth = getAuth() ?: return AuthResult.Error("Firebase not initialized")
        return try {
            val res = auth.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = res.user
            if (user != null) {
                _currentUserState.value = user
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Sign-in failed. Please verify credentials.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Sign-in error")
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): AuthResult {
        val auth = getAuth() ?: return AuthResult.Error("Firebase not initialized")
        return try {
            val res = auth.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = res.user
            if (user != null) {
                _currentUserState.value = user
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Sign-up failed.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Sign-up error")
        }
    }

    suspend fun signInAnonymously(): AuthResult {
        val auth = getAuth() ?: return AuthResult.Error("Firebase not initialized")
        return try {
            val res = auth.signInAnonymously().await()
            val user = res.user
            if (user != null) {
                _currentUserState.value = user
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Anonymous sign-in failed.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Anonymous sign-in error")
        }
    }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): AuthResult {
        val auth = getAuth() ?: return AuthResult.Error("Firebase not initialized")
        return try {
            val res = auth.signInWithCredential(credential).await()
            val user = res.user
            if (user != null) {
                _currentUserState.value = user
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Phone sign-in failed.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Phone credential error")
        }
    }

    fun sendPhoneOtp(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val auth = getAuth() ?: return
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signOut() {
        getAuth()?.signOut()
        _currentUserState.value = null
    }
}
