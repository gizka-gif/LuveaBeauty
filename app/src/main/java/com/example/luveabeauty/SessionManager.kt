package com.example.luveabeauty

import android.content.Context

// Menyimpan sesi login sederhana (nama & email saja, tanpa password)
// agar pengguna tidak perlu login ulang setiap membuka aplikasi.
object SessionManager {
    private const val PREF = "luvea_session"

    fun save(context: Context, name: String, email: String) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putString("name", name)
            .putString("email", email)
            .apply()
    }

    fun get(context: Context): Pair<String, String>? {
        val pref = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val name = pref.getString("name", null)
        val email = pref.getString("email", null)
        return if (name.isNullOrBlank() || email.isNullOrBlank()) null else name to email
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
