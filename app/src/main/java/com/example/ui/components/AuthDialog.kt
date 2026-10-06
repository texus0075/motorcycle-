package com.example.ui.components

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.RacingRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.TrackGreen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun AuthDialog(
    currentUser: FirebaseUser?,
    onDismiss: () -> Unit,
    onEmailSignIn: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onEmailSignUp: (String, String, (Boolean, String?) -> Unit) -> Unit,
    onAnonymousSignIn: ((Boolean, String?) -> Unit) -> Unit,
    onSendPhoneOtp: (String, Activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks) -> Unit,
    onVerifyPhoneCredential: (PhoneAuthCredential, (Boolean, String?) -> Unit) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Email, 1: Phone, 2: Quick/Guest
    var isSignUpMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf("+91") }
    var otpCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        shape = RoundedCornerShape(16.dp),
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(CyanNeon.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (currentUser != null) Icons.Filled.AccountCircle else Icons.Filled.Lock,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (currentUser != null) "Firebase Account" else "Sign In to MotoScope",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = if (currentUser != null)
                        (currentUser.email ?: currentUser.phoneNumber ?: "Anonymous Rider")
                    else
                        "Sync bookmarks, saved reports & cloud garage across devices",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (currentUser != null) {
                    // Profile Info & Sign Out
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate850, RoundedCornerShape(12.dp))
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Authenticated with Firebase",
                            color = TrackGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "UID: ${currentUser.uid.take(12)}...",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onSignOut()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RacingRed, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign Out", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Slate900,
                        contentColor = CyanNeon,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = CyanNeon,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; errorMessage = null },
                            text = { Text("Email", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; errorMessage = null },
                            text = { Text("Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2; errorMessage = null },
                            text = { Text("Quick", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = RacingRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    if (successMessage != null) {
                        Text(
                            text = successMessage ?: "",
                            color = TrackGreen,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    when (selectedTab) {
                        // --- EMAIL / PASSWORD ---
                        0 -> {
                            Column {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email Address") },
                                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = CyanNeon) },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = CyanNeon) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = authTextFieldColors(),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        if (email.isBlank() || password.length < 6) {
                                            errorMessage = "Enter valid email & password (min 6 chars)"
                                            return@Button
                                        }
                                        isLoading = true
                                        errorMessage = null
                                        if (isSignUpMode) {
                                            onEmailSignUp(email, password) { success, err ->
                                                isLoading = false
                                                if (success) onDismiss() else errorMessage = err
                                            }
                                        } else {
                                            onEmailSignIn(email, password) { success, err ->
                                                isLoading = false
                                                if (success) onDismiss() else errorMessage = err
                                            }
                                        }
                                    },
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate900),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Slate900, strokeWidth = 2.dp)
                                    } else {
                                        Text(if (isSignUpMode) "Create Account" else "Sign In", fontWeight = FontWeight.Bold)
                                    }
                                }
                                TextButton(
                                    onClick = { isSignUpMode = !isSignUpMode },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = if (isSignUpMode) "Already have an account? Sign In" else "Don't have an account? Register",
                                        color = Slate400,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // --- PHONE AUTH ---
                        1 -> {
                            Column {
                                if (!isOtpSent) {
                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { phoneNumber = it },
                                        label = { Text("Mobile Number (with country code)") },
                                        leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = AmberOrange) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        colors = authTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            if (activity == null) {
                                                errorMessage = "Activity unavailable for phone verification"
                                                return@Button
                                            }
                                            if (phoneNumber.length < 10) {
                                                errorMessage = "Enter valid phone number"
                                                return@Button
                                            }
                                            isLoading = true
                                            errorMessage = null
                                            onSendPhoneOtp(
                                                phoneNumber,
                                                activity,
                                                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                                        onVerifyPhoneCredential(credential) { success, err ->
                                                            isLoading = false
                                                            if (success) onDismiss() else errorMessage = err
                                                        }
                                                    }

                                                    override fun onVerificationFailed(e: FirebaseException) {
                                                        isLoading = false
                                                        errorMessage = e.localizedMessage ?: "Verification failed"
                                                    }

                                                    override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                                                        isLoading = false
                                                        verificationId = id
                                                        isOtpSent = true
                                                        successMessage = "OTP sent to $phoneNumber"
                                                    }
                                                }
                                            )
                                        },
                                        enabled = !isLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberOrange, contentColor = Slate900),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Slate900, strokeWidth = 2.dp)
                                        } else {
                                            Text("Send SMS Code", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = otpCode,
                                        onValueChange = { otpCode = it },
                                        label = { Text("6-Digit OTP Code") },
                                        leadingIcon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TrackGreen) },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = authTextFieldColors(),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            val vId = verificationId
                                            if (vId == null || otpCode.length < 6) {
                                                errorMessage = "Enter 6-digit verification code"
                                                return@Button
                                            }
                                            val cred = PhoneAuthProvider.getCredential(vId, otpCode.trim())
                                            isLoading = true
                                            onVerifyPhoneCredential(cred) { success, err ->
                                                isLoading = false
                                                if (success) onDismiss() else errorMessage = err
                                            }
                                        },
                                        enabled = !isLoading,
                                        colors = ButtonDefaults.buttonColors(containerColor = TrackGreen, contentColor = Slate900),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Verify & Sign In", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // --- QUICK ACCESS (ANONYMOUS / GUEST) ---
                        2 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        isLoading = true
                                        onAnonymousSignIn { success, err ->
                                            isLoading = false
                                            if (success) onDismiss() else errorMessage = err
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate800, contentColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Continue as Guest Rider", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Slate400)
            }
        }
    )
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = CyanNeon,
    unfocusedBorderColor = Slate700,
    focusedLabelColor = CyanNeon,
    unfocusedLabelColor = Slate400,
    focusedContainerColor = Slate850,
    unfocusedContainerColor = Slate850
)
