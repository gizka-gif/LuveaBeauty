package com.example.luveabeauty

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.ui.theme.*

@Composable
private fun AuthScaffold(content: @Composable ColumnScope.() -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Blush, Cream, Cream)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.luvea_logo),
                contentDescription = "Logo Luvéa Beauty",
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            Spacer(Modifier.height(14.dp))
            Text("Luvéa Beauty", style = MaterialTheme.typography.headlineMedium, color = Raspberry)
            Text(
                "Your beauty, your glow",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = Muted
            )
            Spacer(Modifier.height(26.dp))
            content()
        }
    }
}

@Composable
private fun AuthCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .padding(22.dp),
        content = content
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Danger.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Danger, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(message, style = MaterialTheme.typography.bodySmall, color = Danger)
    }
}

@Composable
private fun PasswordToggle(visible: Boolean, onToggle: () -> Unit) {
    IconButton(onClick = onToggle) {
        Icon(
            imageVector = if (visible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
            contentDescription = if (visible) "Sembunyikan password" else "Tampilkan password",
            tint = Muted
        )
    }
}

@Composable
fun LoginPage(
    onLogin: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    loading: Boolean = false,
    errorMessage: String? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val canSubmit = email.isNotBlank() && password.isNotBlank()

    AuthScaffold {
        AuthCard {
            Text("Selamat datang kembali", style = MaterialTheme.typography.titleLarge, color = Ink)
            Text("Masuk untuk melanjutkan belanja", style = MaterialTheme.typography.bodyMedium, color = Muted)
            Spacer(Modifier.height(20.dp))

            LuveaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Rounded.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(8.dp))
            LuveaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Rounded.Lock,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = { if (canSubmit && !loading) onLogin(email.trim(), password) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { PasswordToggle(passwordVisible) { passwordVisible = !passwordVisible } }
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                ErrorBanner(errorMessage)
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = "Masuk",
                onClick = { onLogin(email.trim(), password) },
                enabled = canSubmit,
                loading = loading,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Belum punya akun?", color = Muted, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onRegisterClick) {
                Text("Daftar", color = Rose, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun RegisterPage(
    onRegister: (String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    loading: Boolean = false,
    errorMessage: String? = null
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val emailError = if (email.isNotBlank() && !emailValid) "Format email belum benar" else null
    val passwordError = if (password.isNotEmpty() && password.length < 6) "Password minimal 6 karakter" else null
    val canSubmit = name.isNotBlank() && emailValid && password.length >= 6

    AuthScaffold {
        AuthCard {
            Text("Buat akun baru", style = MaterialTheme.typography.titleLarge, color = Ink)
            Text("Bergabung dan temukan beauty essentials favoritmu", style = MaterialTheme.typography.bodyMedium, color = Muted)
            Spacer(Modifier.height(20.dp))

            LuveaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nama lengkap",
                leadingIcon = Icons.Rounded.Person
            )
            Spacer(Modifier.height(8.dp))
            LuveaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Rounded.Email,
                keyboardType = KeyboardType.Email,
                errorText = emailError
            )
            Spacer(Modifier.height(8.dp))
            LuveaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password (minimal 6 karakter)",
                leadingIcon = Icons.Rounded.Lock,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                onImeAction = { if (canSubmit && !loading) onRegister(name.trim(), email.trim(), password) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = { PasswordToggle(passwordVisible) { passwordVisible = !passwordVisible } },
                errorText = passwordError
            )

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                ErrorBanner(errorMessage)
            }

            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = "Buat akun",
                onClick = { onRegister(name.trim(), email.trim(), password) },
                enabled = canSubmit,
                loading = loading,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sudah punya akun?", color = Muted, style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = onLoginClick) {
                Text("Masuk", color = Rose, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
