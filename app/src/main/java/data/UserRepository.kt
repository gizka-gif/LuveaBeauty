package com.example.luveabeauty.data

class UserRepository(
    private val userDao: UserDao
) {

    // REGISTER: Membuat akun baru
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Boolean {

        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.isBlank() ||
            cleanEmail.isBlank() ||
            password.isBlank()
        ) {
            return false
        }

        val existingUser = userDao.getUserByEmail(cleanEmail)

        if (existingUser != null) {
            return false
        }

        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        val user = UserEntity(
            name = cleanName,
            email = cleanEmail,
            passwordHash = hash,
            passwordSalt = salt
        )

        userDao.insertUser(user)

        return true
    }

    // LOGIN: Memeriksa email dan password
    suspend fun login(
        email: String,
        password: String
    ): UserEntity? {

        if (email.isBlank() || password.isBlank()) {
            return null
        }

        val cleanEmail = email.trim().lowercase()
        val user = userDao.getUserByEmail(cleanEmail) ?: return null

        val isValid = PasswordHasher.verifyPassword(
            password,
            user.passwordSalt,
            user.passwordHash
        )

        return if (isValid) user else null
    }
}