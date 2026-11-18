package com.example.luveabeauty

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddShoppingCart
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.data.ReviewEntity
import com.example.luveabeauty.ui.theme.*
import kotlinx.coroutines.flow.Flow

// ================================================================ HOME

@Composable
fun PromoPopup(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Raspberry, Rose)))
                    .padding(vertical = 26.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.luvea_logo),
                    contentDescription = "Luvéa Beauty",
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
            }
            Column(
                Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hello, Beauty!", style = MaterialTheme.typography.headlineSmall, color = Raspberry)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Temukan produk makeup, skincare, dan body care favoritmu untuk menemani setiap momen istimewa.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Muted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                PrimaryButton("Belanja sekarang", onDismiss, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun SearchPill(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(1.dp, FieldBorder)
    ) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = Muted)
            Spacer(Modifier.width(10.dp))
            Text("Cari serum, lipstik, sunscreen...", color = Muted, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CategoryTile(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Blush)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Rose, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = RoseDeep)
    }
}

@Composable
fun HomePage(
    products: List<BeautyProduct>,
    userName: String,
    cartCount: Int,
    favoriteIds: Set<Int>,
    onFavorite: (BeautyProduct) -> Unit,
    onProductClick: (BeautyProduct) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onSeeAll: () -> Unit
) {
    val bestSellers = products.sortedByDescending { it.soldCount }.take(5)
    val bestIds = bestSellers.map { it.id }.toSet()
    val recommendations = products.filter { it.id !in bestIds }
    val firstName = userName.trim().substringBefore(" ")

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Halo, $firstName", style = MaterialTheme.typography.bodyMedium, color = Muted)
                Text("Luvéa Beauty", style = MaterialTheme.typography.headlineMedium, color = Raspberry)
            }
            CartAction(cartCount, onCartClick)
        }

        SearchPill(onSearchClick)
        Spacer(Modifier.height(18.dp))

        // Banner utama
        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(Raspberry, Rose)))
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Glow dimulai dari perawatan yang tepat",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Makeup, skincare, dan body care pilihan untuk harimu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = onSeeAll,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Raspberry)
                    ) {
                        Text("Belanja sekarang", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Image(
                    painter = painterResource(id = R.drawable.luvea_logo),
                    contentDescription = "Luvéa Beauty",
                    modifier = Modifier
                        .size(104.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle("Kategori", Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(4.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CategoryTile("Makeup", Icons.Rounded.Brush, Modifier.weight(1f)) { onCategoryClick("Makeup") }
            CategoryTile("Skincare", Icons.Rounded.WaterDrop, Modifier.weight(1f)) { onCategoryClick("Skincare") }
            CategoryTile("Body Care", Icons.Rounded.Spa, Modifier.weight(1f)) { onCategoryClick("Body Care") }
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle(
            "Produk terlaris",
            Modifier.padding(horizontal = 20.dp),
            actionText = "Lihat semua",
            onAction = onSeeAll
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bestSellers, key = { it.id }) { product ->
                ProductTile(
                    product = product,
                    isFavorite = product.id in favoriteIds,
                    onFavorite = { onFavorite(product) },
                    onClick = { onProductClick(product) },
                    modifier = Modifier.width(168.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle("Rekomendasi untukmu", Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(8.dp))
        recommendations.chunked(2).forEach { rowItems ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { product ->
                    ProductTile(
                        product = product,
                        isFavorite = product.id in favoriteIds,
                        onFavorite = { onFavorite(product) },
                        onClick = { onProductClick(product) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

// ================================================================ KATALOG

@Composable
fun CatalogPage(
    products: List<BeautyProduct>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    cartCount: Int,
    favoriteIds: Set<Int>,
    onFavorite: (BeautyProduct) -> Unit,
    onProductClick: (BeautyProduct) -> Unit,
    onCartClick: () -> Unit,
    focusSearch: Boolean,
    onFocusHandled: () -> Unit
) {
    val categories = listOf("All", "Skincare", "Makeup", "Body Care")
    val filtered = products.filter {
        (selectedCategory == "All" || it.category == selectedCategory) &&
                it.name.contains(searchQuery.trim(), ignoreCase = true)
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(focusSearch) {
        if (focusSearch) {
            runCatching { focusRequester.requestFocus() }
            onFocusHandled()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item(key = "header") {
            ScreenHeader(title = "Katalog", actions = { CartAction(cartCount, onCartClick) })
        }
        item(key = "search") {
            SearchField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.padding(horizontal = 20.dp),
                focusRequester = focusRequester
            )
        }
        item(key = "chips") {
            Row(
                Modifier
                    .padding(top = 14.dp)
                    .horizontalScrollRow(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(Modifier.width(12.dp))
                categories.forEach {
                    LuveaChip(
                        label = if (it == "All") "Semua" else it,
                        selected = selectedCategory == it,
                        onClick = { onCategoryChange(it) }
                    )
                }
                Spacer(Modifier.width(12.dp))
            }
        }
        item(key = "count") {
            Text(
                "${filtered.size} produk ditemukan",
                style = MaterialTheme.typography.bodySmall,
                color = Muted,
                modifier = Modifier.padding(start = 20.dp, top = 14.dp, bottom = 10.dp)
            )
        }

        if (filtered.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = "Produk tidak ditemukan",
                    subtitle = "Coba kata kunci lain atau pilih kategori yang berbeda.",
                    actionText = "Reset pencarian",
                    onAction = {
                        onSearchChange("")
                        onCategoryChange("All")
                    }
                )
            }
        } else {
            items(filtered.chunked(2), key = { row -> row.first().id }) { rowItems ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { product ->
                        ProductTile(
                            product = product,
                            isFavorite = product.id in favoriteIds,
                            onFavorite = { onFavorite(product) },
                            onClick = { onProductClick(product) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun Modifier.horizontalScrollRow(): Modifier =
    this.horizontalScroll(rememberScrollState())

// ================================================================ DETAIL

@Composable
fun DetailPage(
    product: BeautyProduct,
    isFavorite: Boolean,
    cartCount: Int,
    reviews: Flow<List<ReviewEntity>>,
    onBack: () -> Unit,
    onFavorite: () -> Unit,
    onCartClick: () -> Unit,
    onAddCart: () -> Unit,
    onBuyNow: () -> Unit,
    onSendReview: (Int, String) -> Unit
) {
    val reviewList by reviews.collectAsState(initial = emptyList())
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val outOfStock = product.stock <= 0

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Blush)
            ) {
                Image(
                    painter = painterResource(id = getProductImage(product)),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Kembali", onBack)
                    Spacer(Modifier.weight(1f))
                    RoundIconButton(
                        icon = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        description = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                        onClick = onFavorite,
                        tint = if (isFavorite) Rose else Ink
                    )
                    Spacer(Modifier.width(10.dp))
                    CartAction(cartCount, onCartClick)
                }
            }

            Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(product.category, style = MaterialTheme.typography.labelLarge, color = Rose)
                Spacer(Modifier.height(4.dp))
                Text(product.name, style = MaterialTheme.typography.headlineMedium, color = Ink)
                Spacer(Modifier.height(8.dp))
                Text(rupiahText(product.price), style = MaterialTheme.typography.headlineSmall, color = Rose)
                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingRow(product.rating, product.soldCount)
                    Spacer(Modifier.weight(1f))
                    val (stockText, stockColor) = when {
                        outOfStock -> "Stok habis" to Danger
                        product.stock <= 5 -> "Tersisa ${product.stock} lagi" to Warning
                        else -> "Stok tersedia (${product.stock})" to Success
                    }
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(stockColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(stockText, color = stockColor, style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("Tentang produk", style = MaterialTheme.typography.titleLarge, color = Ink)
                Spacer(Modifier.height(6.dp))
                Text(product.description, style = MaterialTheme.typography.bodyLarge, color = Muted)

                Spacer(Modifier.height(28.dp))
                Text(
                    "Ulasan (${reviewList.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = Ink
                )
                Spacer(Modifier.height(10.dp))

                if (reviewList.isEmpty()) {
                    Text(
                        "Belum ada ulasan. Jadilah yang pertama berbagi pengalaman.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Muted
                    )
                } else {
                    reviewList.forEach { review ->
                        LuveaCard(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            Row(Modifier.padding(14.dp)) {
                                Box(
                                    Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Blush),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        review.userName.firstOrNull()?.uppercase() ?: "?",
                                        color = RoseDeep,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(review.userName, style = MaterialTheme.typography.titleSmall, color = Ink)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        (1..5).forEach { i ->
                                            Icon(
                                                if (i <= review.rating.toInt()) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                                contentDescription = null,
                                                tint = Gold,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(review.comment, style = MaterialTheme.typography.bodyMedium, color = Ink)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                LuveaCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Tulis ulasan", style = MaterialTheme.typography.titleMedium, color = Ink)
                        Spacer(Modifier.height(8.dp))
                        Row {
                            (1..5).forEach { i ->
                                Icon(
                                    if (i <= rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                                    contentDescription = "Beri $i bintang",
                                    tint = Gold,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .clickable { rating = i }
                                )
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        LuveaTextField(
                            value = comment,
                            onValueChange = { comment = it },
                            label = "Bagikan pengalamanmu",
                            singleLine = false,
                            minLines = 3,
                            imeAction = ImeAction.Default
                        )
                        Spacer(Modifier.height(12.dp))
                        SecondaryButton(
                            text = "Kirim ulasan",
                            onClick = {
                                onSendReview(rating, comment)
                                if (comment.isNotBlank()) comment = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        StickyBar {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(
                    text = "Keranjang",
                    onClick = onAddCart,
                    enabled = !outOfStock,
                    icon = Icons.Rounded.AddShoppingCart,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    text = "Beli sekarang",
                    onClick = onBuyNow,
                    enabled = !outOfStock,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
