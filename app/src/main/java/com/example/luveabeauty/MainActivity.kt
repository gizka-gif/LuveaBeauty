package com.example.luveabeauty

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.withTransaction
import com.example.luveabeauty.data.*
import com.example.luveabeauty.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLuveaEdgeToEdge()

        val userName = intent.getStringExtra("USER_NAME") ?: "Beauty Customer"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            LuvéaBeautyTheme {
                LuveaApp(
                    userName = userName,
                    userEmail = userEmail,
                    database = database
                )
            }
        }
    }
}

private val tabPages = listOf("Home", "Catalog", "Orders", "Profile")

private val seedProducts = listOf(

    // MAKEUP (6 PRODUK)
    ProductEntity(
        name = "Glow Serum",
        category = "Skincare",
        price = 85000,
        emoji = "🧴",
        description = "Serum wajah untuk membantu menjaga kelembapan kulit.",
        rating = 4.8,
        soldCount = 125,
        stock = 30
    ),
    ProductEntity(
        name = "Hydrating Toner",
        category = "Skincare",
        price = 65000,
        emoji = "🧴",
        description = "Toner untuk menyegarkan dan melembapkan wajah.",
        rating = 4.7,
        soldCount = 98,
        stock = 25
    ),
    ProductEntity(
        name = "Soft Matte Lipstick",
        category = "Makeup",
        price = 55000,
        emoji = "💄",
        description = "Lipstick dengan warna cantik untuk aktivitas sehari-hari.",
        rating = 4.9,
        soldCount = 210,
        stock = 40
    ),
    ProductEntity(
        name = "Blush On Pink",
        category = "Makeup",
        price = 45000,
        emoji = "🌸",
        description = "Blush on dengan warna pink yang lembut.",
        rating = 4.6,
        soldCount = 150,
        stock = 35
    ),
    ProductEntity(
        name = "Body Lotion",
        category = "Body Care",
        price = 70000,
        emoji = "🧴",
        description = "Lotion untuk membantu menjaga kelembutan kulit.",
        rating = 4.5,
        soldCount = 87,
        stock = 20
    ),
    ProductEntity(
        name = "Bronzer",
        category = "Makeup",
        price = 65000,
        emoji = "✨",
        description = "Bronzer untuk memberikan efek hangat dan dimensi pada wajah.",
        rating = 4.7,
        soldCount = 75,
        stock = 25
    ),
    ProductEntity(
        name = "Foundation",
        category = "Makeup",
        price = 95000,
        emoji = "💄",
        description = "Foundation untuk membantu meratakan warna kulit wajah.",
        rating = 4.8,
        soldCount = 110,
        stock = 30
    ),
    ProductEntity(
        name = "Eyeshadow",
        category = "Makeup",
        price = 75000,
        emoji = "🎨",
        description = "Eyeshadow dengan pilihan warna untuk berbagai tampilan riasan.",
        rating = 4.7,
        soldCount = 90,
        stock = 25
    ),
    ProductEntity(
        name = "Mascara",
        category = "Makeup",
        price = 55000,
        emoji = "👁️",
        description = "Mascara untuk membantu mempertegas tampilan bulu mata.",
        rating = 4.6,
        soldCount = 85,
        stock = 30
    ),

    // SKINCARE (6 PRODUK)
    ProductEntity(
        name = "Sunscreen",
        category = "Skincare",
        price = 40000,
        emoji = "☀️",
        description = "Sunscreen untuk membantu melindungi kulit dari paparan sinar matahari.",
        rating = 4.8,
        soldCount = 140,
        stock = 35
    ),
    ProductEntity(
        name = "Moisturizer",
        category = "Skincare",
        price = 85000,
        emoji = "🧴",
        description = "Moisturizer untuk membantu menjaga kelembapan kulit.",
        rating = 4.7,
        soldCount = 105,
        stock = 30
    ),
    ProductEntity(
        name = "Cleansing Oil",
        category = "Skincare",
        price = 90000,
        emoji = "🫧",
        description = "Cleansing oil untuk membantu membersihkan makeup dan kotoran.",
        rating = 4.8,
        soldCount = 95,
        stock = 25
    ),
    ProductEntity(
        name = "Masker Wajah",
        category = "Skincare",
        price = 15000,
        emoji = "🌸",
        description = "Masker wajah untuk membantu merawat dan menyegarkan kulit.",
        rating = 4.6,
        soldCount = 80,
        stock = 30
    ),

    // BODY CARE (3 PRODUK)
    ProductEntity(
        name = "Body Wash",
        category = "Body Care",
        price = 60000,
        emoji = "🧴",
        description = "Sabun mandi untuk membersihkan kulit tubuh.",
        rating = 4.7,
        soldCount = 100,
        stock = 30
    ),
    ProductEntity(
        name = "Body Scrub",
        category = "Body Care",
        price = 65000,
        emoji = "✨",
        description = "Body scrub untuk membantu mengangkat sel kulit mati.",
        rating = 4.8,
        soldCount = 90,
        stock = 25
    )
)

@Composable
fun LuveaApp(
    userName: String,
    userEmail: String,
    database: AppDatabase
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Flow dibuat sekali (remember) agar tidak di-collect ulang setiap recomposition
    val productFlow = remember { database.productDao().getAllProducts() }
    val cartFlow = remember { database.cartDao().getAllCartItems() }
    val favoriteFlow = remember { database.favoriteDao().getAllFavorites() }
    val orderFlow = remember { database.orderDao().getAllOrders() }
    val addressFlow = remember { database.addressDao().getMainAddress() }

    val productEntities by productFlow.collectAsState(initial = emptyList())
    val cart by cartFlow.collectAsState(initial = emptyList())
    val favorites by favoriteFlow.collectAsState(initial = emptyList())
    val orders by orderFlow.collectAsState(initial = emptyList())
    val addressEntity by addressFlow.collectAsState(initial = null)

    val products = remember(productEntities) {
        productEntities
            .map {
                BeautyProduct(
                    id = it.id,
                    name = it.name,
                    category = it.category,
                    price = it.price,
                    emoji = it.emoji,
                    description = it.description,
                    rating = it.rating,
                    soldCount = it.soldCount,
                    stock = it.stock
                )
            }
            .distinctBy { it.name.trim().lowercase() }
    }

    var page by rememberSaveable { mutableStateOf("Home") }
    var backStackRaw by rememberSaveable { mutableStateOf("") }
    var selectedProductId by rememberSaveable { mutableIntStateOf(-1) }
    var buyNowProductId by rememberSaveable { mutableIntStateOf(-1) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var orderFilter by rememberSaveable { mutableStateOf("All") }
    var paymentMethod by rememberSaveable { mutableStateOf("COD") }
    var lastOrderNumber by rememberSaveable { mutableStateOf("") }
    var showPromo by rememberSaveable { mutableStateOf(true) }
    var recipient by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var fullAddress by rememberSaveable { mutableStateOf("") }

    var focusCatalogSearch by remember { mutableStateOf(false) }
    var placing by remember { mutableStateOf(false) }
    var payProcessing by remember { mutableStateOf(false) }

    val selectedProduct = products.find { it.id == selectedProductId }
    val buyNowProduct = products.find { it.id == buyNowProductId }
    val cartCount = cart.sumOf { it.quantity }
    val favoriteIds = favorites.map { it.productId }.toSet()
    val unpaidCount = orders.filter { it.status == "Belum Bayar" }.map { it.orderNumber }.distinct().size

    // Item yang dibayar: produk "Beli Sekarang" (tanpa menyentuh keranjang) atau isi keranjang
    val checkoutItems: List<CartEntity> = if (buyNowProduct != null) {
        listOf(
            CartEntity(
                productId = buyNowProduct.id,
                productName = buyNowProduct.name,
                category = buyNowProduct.category,
                price = buyNowProduct.price,
                emoji = buyNowProduct.emoji,
                quantity = 1
            )
        )
    } else cart

    // Data awal produk (hanya ditambahkan jika belum ada)
    LaunchedEffect(Unit) {
        seedProducts.forEach { product ->
            if (database.productDao().getProductByName(product.name) == null) {
                database.productDao().insertProduct(product)
            }
        }
    }

    // Isi form alamat dari alamat tersimpan; jika belum ada, pakai nama akun
    LaunchedEffect(addressEntity) {
        val saved = addressEntity
        if (saved != null) {
            recipient = saved.recipientName
            phone = saved.phoneNumber
            fullAddress = saved.fullAddress
        } else if (recipient.isBlank()) {
            recipient = userName
        }
    }

    // ------------------------------------------------------------ Navigasi

    fun stackList(): List<String> =
        if (backStackRaw.isEmpty()) emptyList() else backStackRaw.split(",")

    fun navigate(to: String) {
        if (to == page) return
        backStackRaw = (stackList() + page).joinToString(",")
        page = to
    }

    fun switchTab(tab: String) {
        backStackRaw = ""
        buyNowProductId = -1
        page = tab
    }

    fun resetTo(to: String) {
        backStackRaw = ""
        page = to
    }

    fun goBack() {
        if (page == "Checkout") buyNowProductId = -1
        val stack = stackList()
        if (stack.isEmpty()) {
            page = "Home"
        } else {
            page = stack.last()
            backStackRaw = stack.dropLast(1).joinToString(",")
        }
    }

    // Tombol Back sistem: kembali ke halaman sebelumnya; keluar aplikasi hanya dari Home
    BackHandler(enabled = page != "Home") { goBack() }

    // ------------------------------------------------------------ Aksi

    fun showMessage(message: String, actionLabel: String? = null, onAction: () -> Unit = {}) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) onAction()
        }
    }

    fun openOrderDetail(orderNumber: String) {
        val detailIntent = Intent(context, OrderDetailActivity::class.java).apply {
            putExtra("ORDER_NUMBER", orderNumber)
            putExtra("USER_NAME", userName)
            putExtra("USER_EMAIL", userEmail)
        }
        context.startActivity(detailIntent)
    }

    fun toggleFavorite(product: BeautyProduct) {
        scope.launch {
            val existing = database.favoriteDao().getFavorite(product.id)
            if (existing != null) {
                database.favoriteDao().deleteFavorite(existing)
                showMessage("Dihapus dari favorit")
            } else {
                database.favoriteDao().insertFavorite(
                    FavoriteEntity(
                        productId = product.id,
                        productName = product.name,
                        category = product.category,
                        price = product.price,
                        emoji = product.emoji
                    )
                )
                showMessage("Ditambahkan ke favorit")
            }
        }
    }

    fun addToCart(product: BeautyProduct) {
        scope.launch {
            val currentProduct = database.productDao().getProductById(product.id)

            if (currentProduct == null) {
                showMessage("Produk sudah tidak tersedia")
                return@launch
            }
            if (currentProduct.stock <= 0) {
                showMessage("Stok produk habis")
                return@launch
            }

            val existing = database.cartDao().getCartItem(product.id)
            if (existing != null) {
                if (existing.quantity >= currentProduct.stock) {
                    showMessage("Jumlah melebihi stok tersedia")
                } else {
                    database.cartDao().updateCartItem(existing.copy(quantity = existing.quantity + 1))
                    showMessage("Jumlah produk diperbarui", "Lihat keranjang") { navigate("Cart") }
                }
            } else {
                database.cartDao().insertCartItem(
                    CartEntity(
                        productId = product.id,
                        productName = currentProduct.name,
                        category = currentProduct.category,
                        price = currentProduct.price,
                        emoji = currentProduct.emoji,
                        quantity = 1
                    )
                )
                showMessage("Ditambahkan ke keranjang", "Lihat keranjang") { navigate("Cart") }
            }
        }
    }

    // "Beli Sekarang" tidak lagi mengosongkan keranjang yang sudah ada
    fun startBuyNow(product: BeautyProduct) {
        buyNowProductId = product.id
        navigate("Checkout")
    }

    fun changeQuantity(item: CartEntity, quantity: Int) {
        scope.launch {
            if (quantity <= 0) {
                database.cartDao().deleteCartItem(item)
                return@launch
            }
            val product = database.productDao().getProductById(item.productId)
            if (product == null) {
                showMessage("Produk sudah tidak tersedia")
                database.cartDao().deleteCartItem(item)
            } else if (quantity > product.stock) {
                showMessage("Jumlah melebihi stok tersedia: ${product.stock}")
            } else {
                database.cartDao().updateCartItem(item.copy(quantity = quantity))
            }
        }
    }

    fun placeOrder() {
        if (placing) return
        val items = checkoutItems
        val isBuyNow = buyNowProduct != null

        if (items.isEmpty()) {
            showMessage("Keranjang masih kosong")
            return
        }
        if (recipient.isBlank() ||
            phone.count { it.isDigit() } !in 10..15 ||
            fullAddress.trim().length < 10
        ) {
            showMessage("Periksa kembali data alamat dan nomor telepon")
            return
        }
        if (paymentMethod !in listOf("Transfer Bank", "E-wallet", "COD")) {
            showMessage("Pilih metode pembayaran")
            return
        }

        scope.launch {
            placing = true
            try {
                val orderNumber = "LV${System.currentTimeMillis()}"
                val status = if (paymentMethod == "COD") "Dikemas" else "Belum Bayar"

                // Semua proses database dijalankan dalam satu transaksi
                val validationMessage = database.withTransaction {
                    val checked = mutableListOf<Pair<CartEntity, ProductEntity>>()

                    for (item in items) {
                        if (item.quantity <= 0) {
                            return@withTransaction "Jumlah produk tidak valid"
                        }
                        val product = database.productDao().getProductById(item.productId)
                            ?: return@withTransaction "Produk ${item.productName} sudah tidak tersedia"
                        if (product.stock < item.quantity) {
                            return@withTransaction "Stok ${item.productName} tidak mencukupi"
                        }
                        checked.add(item to product)
                    }

                    checked.forEach { (item, product) ->
                        database.productDao().updateProduct(
                            product.copy(
                                stock = product.stock - item.quantity,
                                soldCount = product.soldCount + item.quantity
                            )
                        )
                    }

                    database.addressDao().saveAddress(
                        AddressEntity(
                            recipientName = recipient.trim(),
                            phoneNumber = phone.trim(),
                            fullAddress = fullAddress.trim()
                        )
                    )

                    database.orderDao().insertOrders(
                        items.map { item ->
                            OrderEntity(
                                orderNumber = orderNumber,
                                productId = item.productId,
                                productName = item.productName,
                                price = item.price,
                                quantity = item.quantity,
                                totalPrice = item.price * item.quantity,
                                address = fullAddress.trim(),
                                paymentMethod = paymentMethod,
                                status = status
                            )
                        }
                    )

                    // Keranjang hanya dikosongkan jika pesanan berasal dari keranjang
                    if (!isBuyNow) database.cartDao().clearCart()

                    null
                }

                if (validationMessage != null) {
                    showMessage(validationMessage)
                } else {
                    lastOrderNumber = orderNumber
                    buyNowProductId = -1
                    resetTo(if (paymentMethod == "COD") "Success" else "Payment")
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                showMessage("Pesanan gagal diproses. Silakan coba lagi.")
            } finally {
                placing = false
            }
        }
    }

    fun payOrder() {
        if (payProcessing) return
        scope.launch {
            payProcessing = true
            delay(900) // simulasi proses pembayaran
            val existing = database.orderDao().getOrdersByNumber(lastOrderNumber)
            when {
                existing.isEmpty() -> {
                    showMessage("Data pesanan tidak ditemukan")
                    resetTo("Orders")
                }
                existing.first().status != "Belum Bayar" -> {
                    showMessage("Pesanan ini sudah dibayar")
                    resetTo("Orders")
                }
                else -> {
                    database.orderDao().updateOrderStatus(lastOrderNumber, "Dikemas")
                    resetTo("Success")
                }
            }
            payProcessing = false
        }
    }

    // Pembatalan mengembalikan stok produk
    fun cancelOrder(number: String) {
        scope.launch {
            val cancelled = database.withTransaction {
                val items = database.orderDao().getOrdersByNumber(number)
                val status = items.firstOrNull()?.status
                if (status != "Belum Bayar" && status != "Dikemas") {
                    return@withTransaction false
                }
                items.forEach { item ->
                    val product = database.productDao().getProductById(item.productId)
                    if (product != null) {
                        database.productDao().updateProduct(
                            product.copy(
                                stock = product.stock + item.quantity,
                                soldCount = (product.soldCount - item.quantity).coerceAtLeast(0)
                            )
                        )
                    }
                }
                database.orderDao().updateOrderStatus(number, "Dibatalkan")
                true
            }
            showMessage(if (cancelled) "Pesanan dibatalkan" else "Pesanan ini tidak bisa dibatalkan")
        }
    }

    fun logout() {
        SessionManager.clear(context)
        Toast.makeText(context, "Kamu telah keluar", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    // ------------------------------------------------------------ Tampilan

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            AnimatedVisibility(
                visible = page in tabPages,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                NavigationBar(containerColor = Color.White) {
                    val items = listOf(
                        Triple("Home", "Beranda", Icons.Rounded.Home),
                        Triple("Catalog", "Katalog", Icons.Rounded.GridView),
                        Triple("Orders", "Pesanan", Icons.Rounded.Inventory2),
                        Triple("Profile", "Profil", Icons.Rounded.Person)
                    )
                    items.forEach { (tab, label, icon) ->
                        NavigationBarItem(
                            selected = page == tab,
                            onClick = {
                                if (page != tab) {
                                    if (tab == "Catalog") {
                                        selectedCategory = "All"
                                        searchQuery = ""
                                    }
                                    switchTab(tab)
                                }
                            },
                            icon = {
                                if (tab == "Orders" && unpaidCount > 0) {
                                    BadgedBox(badge = { Badge(containerColor = Rose) { Text("$unpaidCount") } }) {
                                        Icon(icon, contentDescription = label)
                                    }
                                } else {
                                    Icon(icon, contentDescription = label)
                                }
                            },
                            label = { Text(label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Rose,
                                selectedTextColor = Rose,
                                indicatorColor = Blush,
                                unselectedIconColor = Muted,
                                unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
        ) {
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (fadeIn(tween(220, delayMillis = 60)) + slideInVertically(tween(260)) { it / 24 }) togetherWith
                            fadeOut(tween(120))
                },
                label = "page"
            ) { target ->
                when (target) {
                    "Home" -> HomePage(
                        products = products,
                        userName = userName,
                        cartCount = cartCount,
                        favoriteIds = favoriteIds,
                        onFavorite = { toggleFavorite(it) },
                        onProductClick = {
                            selectedProductId = it.id
                            navigate("Detail")
                        },
                        onCategoryClick = {
                            selectedCategory = it
                            searchQuery = ""
                            switchTab("Catalog")
                        },
                        onSearchClick = {
                            focusCatalogSearch = true
                            switchTab("Catalog")
                        },
                        onCartClick = { navigate("Cart") },
                        onSeeAll = {
                            selectedCategory = "All"
                            searchQuery = ""
                            switchTab("Catalog")
                        }
                    )

                    "Catalog" -> CatalogPage(
                        products = products,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedCategory = selectedCategory,
                        onCategoryChange = { selectedCategory = it },
                        cartCount = cartCount,
                        favoriteIds = favoriteIds,
                        onFavorite = { toggleFavorite(it) },
                        onProductClick = {
                            selectedProductId = it.id
                            navigate("Detail")
                        },
                        onCartClick = { navigate("Cart") },
                        focusSearch = focusCatalogSearch,
                        onFocusHandled = { focusCatalogSearch = false }
                    )

                    "Detail" -> {
                        val product = selectedProduct
                        if (product == null) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Rose)
                            }
                        } else {
                            val reviewFlow = remember(product.id) {
                                database.reviewDao().getReviewsByProduct(product.id)
                            }
                            DetailPage(
                                product = product,
                                isFavorite = product.id in favoriteIds,
                                cartCount = cartCount,
                                reviews = reviewFlow,
                                onBack = { goBack() },
                                onFavorite = { toggleFavorite(product) },
                                onCartClick = { navigate("Cart") },
                                onAddCart = { addToCart(product) },
                                onBuyNow = { startBuyNow(product) },
                                onSendReview = { rating, comment ->
                                    if (comment.isBlank()) {
                                        showMessage("Tulis ulasan terlebih dahulu")
                                    } else {
                                        scope.launch {
                                            database.reviewDao().insertReview(
                                                ReviewEntity(
                                                    productId = product.id,
                                                    userName = userName,
                                                    rating = rating.toFloat(),
                                                    comment = comment.trim()
                                                )
                                            )
                                            showMessage("Ulasan berhasil dikirim")
                                        }
                                    }
                                }
                            )
                        }
                    }

                    "Cart" -> CartPage(
                        cart = cart,
                        onBack = { goBack() },
                        onCheckout = {
                            buyNowProductId = -1
                            navigate("Checkout")
                        },
                        onRemove = { item -> scope.launch { database.cartDao().deleteCartItem(item) } },
                        onQuantityChange = { item, quantity -> changeQuantity(item, quantity) },
                        onShop = { switchTab("Catalog") }
                    )

                    "Checkout" -> CheckoutPage(
                        items = checkoutItems,
                        recipient = recipient,
                        phone = phone,
                        address = fullAddress,
                        paymentMethod = paymentMethod,
                        placing = placing,
                        onRecipientChange = { recipient = it },
                        onPhoneChange = { phone = it },
                        onAddressChange = { fullAddress = it },
                        onPaymentChange = { paymentMethod = it },
                        onPlaceOrder = { placeOrder() },
                        onBack = { goBack() }
                    )

                    "Payment" -> PaymentPage(
                        method = orders.firstOrNull { it.orderNumber == lastOrderNumber }?.paymentMethod
                            ?: paymentMethod,
                        orderNumber = lastOrderNumber,
                        total = orders.filter { it.orderNumber == lastOrderNumber }.sumOf { it.totalPrice },
                        processing = payProcessing,
                        onPaid = { payOrder() },
                        onLater = { switchTab("Orders") },
                        onBack = { goBack() }
                    )

                    "Success" -> SuccessPage(
                        orderNumber = lastOrderNumber,
                        onOrders = { switchTab("Orders") },
                        onHome = { switchTab("Home") }
                    )

                    "Orders" -> OrdersPage(
                        orders = orders,
                        selectedFilter = orderFilter,
                        onFilterChange = { orderFilter = it },
                        onPay = { number ->
                            lastOrderNumber = number
                            navigate("Payment")
                        },
                        onCancel = { cancelOrder(it) },
                        onAdvance = { number, currentStatus ->
                            val next = when (currentStatus) {
                                "Dikemas" -> "Dikirim"
                                "Dikirim" -> "Selesai"
                                else -> currentStatus
                            }
                            scope.launch { database.orderDao().updateOrderStatus(number, next) }
                        },
                        onDelete = { number -> scope.launch { database.orderDao().deleteOrderByNumber(number) } },
                        onDetail = { openOrderDetail(it) },
                        onShop = { switchTab("Catalog") }
                    )

                    "Profile" -> ProfilePage(
                        userName = userName,
                        userEmail = userEmail,
                        favoriteCount = favorites.size,
                        orderCount = orders.map { it.orderNumber }.distinct().size,
                        onFavorites = { navigate("Favorite") },
                        onOrders = { switchTab("Orders") },
                        onAddress = { navigate("Address") },
                        onAbout = { navigate("About") },
                        onLogout = { logout() }
                    )

                    "Favorite" -> FavoritesPage(
                        favorites = favorites,
                        onBack = { goBack() },
                        onRemove = { favorite -> scope.launch { database.favoriteDao().deleteFavorite(favorite) } },
                        onProductClick = { favorite ->
                            if (products.any { it.id == favorite.productId }) {
                                selectedProductId = favorite.productId
                                navigate("Detail")
                            } else {
                                showMessage("Produk sudah tidak tersedia")
                            }
                        },
                        onShop = { switchTab("Catalog") }
                    )

                    "Address" -> AddressPage(
                        recipient = recipient,
                        phone = phone,
                        address = fullAddress,
                        onRecipientChange = { recipient = it },
                        onPhoneChange = { phone = it },
                        onAddressChange = { fullAddress = it },
                        onSave = {
                            scope.launch {
                                database.addressDao().saveAddress(
                                    AddressEntity(
                                        recipientName = recipient.trim(),
                                        phoneNumber = phone.trim(),
                                        fullAddress = fullAddress.trim()
                                    )
                                )
                                showMessage("Alamat berhasil disimpan")
                                goBack()
                            }
                        },
                        onBack = { goBack() }
                    )

                    "About" -> AboutPage(onBack = { goBack() })

                    else -> {}
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Ink,
                    contentColor = Color.White,
                    actionColor = Blush,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }

    if (showPromo && page == "Home") {
        PromoPopup(onDismiss = { showPromo = false })
    }
}
