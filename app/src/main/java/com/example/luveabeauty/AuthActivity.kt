package com.example.luveabeauty

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch
import com.example.luveabeauty.data.AppDatabase
import com.example.luveabeauty.data.UserRepository
import com.example.luveabeauty.ui.theme.LuvéaBeautyTheme
import com.example.luveabeauty.ui.theme.enableLuveaEdgeToEdge

class AuthActivity : ComponentActivity() {

    private fun openMain(name: String, email: String) {
        SessionManager.save(this, name, email)
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER_NAME", name)
            putExtra("USER_EMAIL", email)
        }
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLuveaEdgeToEdge()

        // Sudah pernah login: langsung masuk ke aplikasi
        val session = SessionManager.get(this)
        if (session != null) {
            openMain(session.first, session.second)
            return
        }

        val repository = UserRepository(
            AppDatabase.getDatabase(applicationContext).userDao()
        )

        setContent {
            LuvéaBeautyTheme {
                var authPage by rememberSaveable { mutableStateOf("Login") }
                var loading by remember { mutableStateOf(false) }
                var error by remember { mutableStateOf<String?>(null) }
                val scope = rememberCoroutineScope()

                AnimatedContent(
                    targetState = authPage,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "auth"
                ) { target ->
                    if (target == "Login") {
                        LoginPage(
                            loading = loading,
                            errorMessage = error,
                            onLogin = { email, password ->
                                scope.launch {
                                    loading = true
                                    error = null
                                    val user = repository.login(email, password)
                                    loading = false
                                    if (user != null) {
                                        openMain(user.name, user.email)
                                    } else {
                                        error = "Email atau password salah."
                                    }
                                }
                            },
                            onRegisterClick = {
                                error = null
                                authPage = "Register"
                            }
                        )
                    } else {
                        RegisterPage(
                            loading = loading,
                            errorMessage = error,
                            onRegister = { name, email, password ->
                                scope.launch {
                                    loading = true
                                    error = null
                                    val success = repository.register(name, email, password)
                                    if (success) {
                                        // Akun jadi -> langsung masuk tanpa login ulang
                                        val user = repository.login(email, password)
                                        loading = false
                                        if (user != null) {
                                            openMain(user.name, user.email)
                                        } else {
                                            authPage = "Login"
                                        }
                                    } else {
                                        loading = false
                                        error = "Email sudah terdaftar."
                                    }
                                }
                            },
                            onLoginClick = {
                                error = null
                                authPage = "Login"
                            }
                        )
                    }
                }
            }
        }
    }
}
