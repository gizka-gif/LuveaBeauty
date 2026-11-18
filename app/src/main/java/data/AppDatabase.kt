package com.example.luveabeauty.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        CartEntity::class,
        FavoriteEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        AddressEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun orderDao(): OrderDao
    abstract fun reviewDao(): ReviewDao
    abstract fun addressDao(): AddressDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN rating REAL NOT NULL DEFAULT 4.4")
                db.execSQL("ALTER TABLE products ADD COLUMN soldCount INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE products ADD COLUMN stock INTEGER NOT NULL DEFAULT 20")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS cart (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        productName TEXT NOT NULL,
                        category TEXT NOT NULL,
                        price INTEGER NOT NULL,
                        emoji TEXT NOT NULL,
                        quantity INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS favorites (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        productName TEXT NOT NULL,
                        category TEXT NOT NULL,
                        price INTEGER NOT NULL,
                        emoji TEXT NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        orderNumber TEXT NOT NULL,
                        productId INTEGER NOT NULL,
                        productName TEXT NOT NULL,
                        price INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        totalPrice INTEGER NOT NULL,
                        address TEXT NOT NULL,
                        paymentMethod TEXT NOT NULL,
                        status TEXT NOT NULL,
                        orderDate INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS reviews (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        productId INTEGER NOT NULL,
                        userName TEXT NOT NULL,
                        rating REAL NOT NULL,
                        comment TEXT NOT NULL,
                        reviewDate INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS addresses (
                        id INTEGER NOT NULL PRIMARY KEY,
                        recipientName TEXT NOT NULL,
                        phoneNumber TEXT NOT NULL,
                        fullAddress TEXT NOT NULL,
                        isMainAddress INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "luvea_beauty_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}