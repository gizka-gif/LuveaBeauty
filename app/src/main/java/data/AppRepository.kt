
package com.example.luveabeauty.data

class AppRepository(
    private val database: AppDatabase
) {
    // DATA REACTIVE
    val products = database.productDao().getAllProducts()
    val cartItems = database.cartDao().getAllCartItems()
    val favorites = database.favoriteDao().getAllFavorites()
    val orders = database.orderDao().getAllOrders()
    val mainAddress = database.addressDao().getMainAddress()

    // PRODUCT CRUD
    suspend fun addProduct(product: ProductEntity) {
        database.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        database.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        database.productDao().deleteProduct(product)
    }

    suspend fun getProductById(productId: Int): ProductEntity? {
        return database.productDao().getProductById(productId)
    }

    // CART
    suspend fun addToCart(item: CartEntity) {
        val existing = database.cartDao().getCartItem(item.productId)

        if (existing == null) {
            database.cartDao().insertCartItem(item)
        } else {
            database.cartDao().updateCartItem(
                existing.copy(
                    quantity = existing.quantity + item.quantity
                )
            )
        }
    }

    suspend fun updateCart(item: CartEntity) {
        database.cartDao().updateCartItem(item)
    }

    suspend fun removeFromCart(item: CartEntity) {
        database.cartDao().deleteCartItem(item)
    }

    suspend fun clearCart() {
        database.cartDao().clearCart()
    }

    // FAVORITES
    suspend fun toggleFavorite(product: FavoriteEntity) {
        val existing = database.favoriteDao()
            .getFavorite(product.productId)

        if (existing == null) {
            database.favoriteDao().insertFavorite(product)
        } else {
            database.favoriteDao().deleteFavorite(existing)
        }
    }

    // ORDERS
    suspend fun createOrder(order: OrderEntity) {
        database.orderDao().insertOrder(order)
    }

    suspend fun createOrders(orders: List<OrderEntity>) {
        database.orderDao().insertOrders(orders)
    }

    suspend fun updateOrderStatus(
        orderNumber: String,
        status: String
    ) {
        database.orderDao().updateOrderStatus(orderNumber, status)
    }

    suspend fun deleteOrder(orderId: Int) {
        database.orderDao().deleteOrder(orderId)
    }

    suspend fun deleteOrderByNumber(orderNumber: String) {
        database.orderDao().deleteOrderByNumber(orderNumber)
    }

    // REVIEWS
    fun getReviews(productId: Int) =
        database.reviewDao().getReviewsByProduct(productId)

    suspend fun addReview(review: ReviewEntity) {
        database.reviewDao().insertReview(review)
    }

    suspend fun deleteReview(review: ReviewEntity) {
        database.reviewDao().deleteReview(review)
    }

    // ADDRESS
    suspend fun saveAddress(address: AddressEntity) {
        database.addressDao().saveAddress(address)
    }

    suspend fun deleteAddress() {
        database.addressDao().deleteAddress()
    }
}
