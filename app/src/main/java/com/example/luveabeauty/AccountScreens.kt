package com.example.luveabeauty

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.data.FavoriteEntity
import com.example.luveabeauty.data.OrderEntity
import com.example.luveabeauty.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ================================================================ PESANAN

private data class OrderConfirm(val number: String, val isCancel: Boolean)

@Composable
fun OrdersPage(
    orders: List<OrderEntity>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onPay: (String) -> Unit,
    onCancel: (String) -> Unit,
    onAdvance: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onDetail: (String) -> Unit,
    onShop: () -> Unit
) {
    val filters = listOf("All", "Belum Bayar", "Dikemas", "Dikirim", "Selesai", "Dibatalkan")
    val grouped = orders.groupBy { it.orderNumber }.values.toList()
    val filtered = grouped.filter { selectedFilter == "All" || it.first().status == selectedFilter }
    var confirm by remember { mutableStateOf<OrderConfirm?>(null) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Pesanan saya")

        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach {
                LuveaChip(
                    label = if (it == "All") "Semua" else it,
                    selected = selectedFilter == it,
                    onClick = { onFilterChange(it) }
                )
            }
        }
        Spacer(Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.Inventory2,
                title = if (selectedFilter == "All") "Belum ada pesanan" else "Tidak ada pesanan $selectedFilter",
                subtitle = "Pesanan yang kamu buat akan muncul di sini.",
                modifier = Modifier.weight(1f),
                actionText = if (selectedFilter == "All") "Mulai belanja" else null,
                onAction = onShop
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filtered, key = { it.first().orderNumber }) { group ->
                    OrderCard(
                        group = group,
                        onPay = onPay,
                        onAdvance = onAdvance,
                        onDetail = onDetail,
                        onAskCancel = { confirm = OrderConfirm(it, true) },
                        onAskDelete = { confirm = OrderConfirm(it, false) }
                    )
                }
            }
        }
    }

    confirm?.let { c ->
        AlertDialog(
            onDismissRequest = { confirm = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text(if (c.isCancel) "Batalkan pesanan?" else "Hapus pesanan?") },
            text = {
                Text(
                    if (c.isCancel) "Pesanan ditandai dibatalkan dan stok produk dikembalikan."
                    else "Pesanan dihapus dari riwayat dan tidak bisa dikembalikan."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (c.isCancel) onCancel(c.number) else onDelete(c.number)
                    confirm = null
                }) {
                    Text(if (c.isCancel) "Ya, batalkan" else "Hapus", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirm = null }) { Text("Kembali", color = Muted) }
            }
        )
    }
}

@Composable
private fun OrderCard(
    group: List<OrderEntity>,
    onPay: (String) -> Unit,
    onAdvance: (String, String) -> Unit,
    onDetail: (String) -> Unit,
    onAskCancel: (String) -> Unit,
    onAskDelete: (String) -> Unit
) {
    val first = group.first()
    val total = group.sumOf { it.totalPrice }
    val dateText = remember(first.orderDate) {
        SimpleDateFormat("d MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(first.orderDate))
    }

    LuveaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(first.orderNumber, style = MaterialTheme.typography.titleSmall, color = Ink)
                    Text(dateText, style = MaterialTheme.typography.labelSmall, color = Muted)
                }
                StatusChip(first.status)
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Line)

            group.forEachIndexed { index, item ->
                if (index > 0) Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = getProductImageByName(item.productName)),
                        contentDescription = item.productName,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(item.productName, style = MaterialTheme.typography.titleSmall, color = Ink)
                        Text("${item.quantity} x ${rupiahText(item.price)}", style = MaterialTheme.typography.bodySmall, color = Muted)
                    }
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Line)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Total pesanan", style = MaterialTheme.typography.labelMedium, color = Muted)
                    Text("Bayar: ${first.paymentMethod}", style = MaterialTheme.typography.labelSmall, color = Muted)
                }
                Text(rupiahText(total), style = MaterialTheme.typography.titleMedium, color = Rose)
            }

            Spacer(Modifier.height(14.dp))

            when (first.status) {
                "Belum Bayar" -> PrimaryButton("Bayar sekarang", { onPay(first.orderNumber) }, Modifier.fillMaxWidth())
                "Dikemas" -> PrimaryButton("Simulasikan pengiriman", { onAdvance(first.orderNumber, first.status) }, Modifier.fillMaxWidth())
                "Dikirim" -> PrimaryButton("Simulasikan pesanan selesai", { onAdvance(first.orderNumber, first.status) }, Modifier.fillMaxWidth())
            }
            if (first.status in listOf("Belum Bayar", "Dikemas", "Dikirim")) Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SecondaryButton("Lihat detail", { onDetail(first.orderNumber) }, Modifier.weight(1f))
                when (first.status) {
                    "Belum Bayar", "Dikemas" ->
                        SecondaryButton("Batalkan", { onAskCancel(first.orderNumber) }, Modifier.weight(1f), color = Danger)
                    "Selesai", "Dibatalkan" ->
                        SecondaryButton("Hapus", { onAskDelete(first.orderNumber) }, Modifier.weight(1f), color = Danger)
                }
            }
        }
    }
}

// ================================================================ PROFIL

@Composable
private fun MenuRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Blush),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Rose, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, color = Ink, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = Muted)
    }
}

@Composable
private fun StatTile(count: Int, label: String, modifier: Modifier, onClick: () -> Unit) {
    LuveaCard(modifier = modifier, onClick = onClick) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", style = MaterialTheme.typography.headlineSmall, color = Rose)
            Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }
}

@Composable
fun ProfilePage(
    userName: String,
    userEmail: String,
    favoriteCount: Int,
    orderCount: Int,
    onFavorites: () -> Unit,
    onOrders: () -> Unit,
    onAddress: () -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit
) {
    var askLogout by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        ScreenHeader(title = "Profil")

        Row(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(Raspberry, Rose)))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    userName.trim().firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Raspberry
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(userName, style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text(userEmail, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            }
        }

        Spacer(Modifier.height(14.dp))
        Row(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatTile(favoriteCount, "Favorit", Modifier.weight(1f), onFavorites)
            StatTile(orderCount, "Pesanan", Modifier.weight(1f), onOrders)
        }

        Spacer(Modifier.height(14.dp))
        LuveaCard(Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
            MenuRow(Icons.Rounded.Favorite, "Produk favorit", onFavorites)
            HorizontalDivider(color = Line)
            MenuRow(Icons.Rounded.Inventory2, "Pesanan saya", onOrders)
            HorizontalDivider(color = Line)
            MenuRow(Icons.Rounded.LocationOn, "Alamat pengiriman", onAddress)
            HorizontalDivider(color = Line)
            MenuRow(Icons.Rounded.Info, "Tentang Luvéa Beauty", onAbout)
        }

        Spacer(Modifier.height(20.dp))
        SecondaryButton(
            text = "Keluar",
            onClick = { askLogout = true },
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            icon = Icons.AutoMirrored.Rounded.Logout,
            color = Danger
        )
    }

    if (askLogout) {
        AlertDialog(
            onDismissRequest = { askLogout = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Keluar dari akun?") },
            text = { Text("Kamu perlu masuk lagi untuk melanjutkan belanja.") },
            confirmButton = {
                TextButton(onClick = {
                    askLogout = false
                    onLogout()
                }) { Text("Keluar", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { askLogout = false }) { Text("Batal", color = Muted) }
            }
        )
    }
}

// ================================================================ FAVORIT

@Composable
fun FavoritesPage(
    favorites: List<FavoriteEntity>,
    onBack: () -> Unit,
    onRemove: (FavoriteEntity) -> Unit,
    onProductClick: (FavoriteEntity) -> Unit,
    onShop: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Produk favorit", onBack = onBack)

        if (favorites.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Belum ada favorit",
                subtitle = "Ketuk ikon hati pada produk untuk menyimpannya di sini.",
                modifier = Modifier.weight(1f),
                actionText = "Jelajahi produk",
                onAction = onShop
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites, key = { it.id }) { fav ->
                    LuveaCard(Modifier.fillMaxWidth(), onClick = { onProductClick(fav) }) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = getProductImageByName(fav.productName)),
                                contentDescription = fav.productName,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(fav.category, style = MaterialTheme.typography.labelSmall, color = Muted)
                                Text(fav.productName, style = MaterialTheme.typography.titleSmall, color = Ink)
                                Text(rupiahText(fav.price), style = MaterialTheme.typography.titleSmall, color = Rose)
                            }
                            IconButton(onClick = { onRemove(fav) }) {
                                Icon(Icons.Rounded.Favorite, contentDescription = "Hapus dari favorit", tint = Rose)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ================================================================ ALAMAT

@Composable
fun AddressPage(
    recipient: String,
    phone: String,
    address: String,
    onRecipientChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    var recipientError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Alamat pengiriman", onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            SectionCard("Alamat utama") {
                LuveaTextField(
                    value = recipient,
                    onValueChange = { onRecipientChange(it); recipientError = false },
                    label = "Nama penerima",
                    leadingIcon = Icons.Rounded.Person,
                    errorText = if (recipientError) "Nama penerima wajib diisi" else null
                )
                Spacer(Modifier.height(4.dp))
                LuveaTextField(
                    value = phone,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() || c == '+' || c == ' ' || c == '-' }) {
                            onPhoneChange(it)
                            phoneError = false
                        }
                    },
                    label = "Nomor telepon",
                    leadingIcon = Icons.Rounded.Phone,
                    keyboardType = KeyboardType.Phone,
                    errorText = if (phoneError) "Nomor telepon harus 10–15 digit" else null
                )
                Spacer(Modifier.height(4.dp))
                LuveaTextField(
                    value = address,
                    onValueChange = { onAddressChange(it); addressError = false },
                    label = "Alamat lengkap",
                    leadingIcon = Icons.Rounded.LocationOn,
                    singleLine = false,
                    minLines = 3,
                    imeAction = ImeAction.Default,
                    errorText = if (addressError) "Alamat minimal 10 karakter" else null
                )
            }
        }

        StickyBar {
            PrimaryButton(
                text = "Simpan alamat",
                onClick = {
                    recipientError = recipient.isBlank()
                    phoneError = phone.count { it.isDigit() } !in 10..15
                    addressError = address.trim().length < 10
                    if (!recipientError && !phoneError && !addressError) onSave()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ================================================================ TENTANG

@Composable
fun AboutPage(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Tentang", onBack = onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.luvea_logo),
                contentDescription = "Logo Luvéa Beauty",
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(26.dp))
            )
            Spacer(Modifier.height(14.dp))
            Text("Luvéa Beauty", style = MaterialTheme.typography.headlineMedium, color = Raspberry)
            Text(
                "Your beauty, your glow",
                style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                color = Muted
            )
            Spacer(Modifier.height(22.dp))
            LuveaCard(Modifier.fillMaxWidth()) {
                Text(
                    "Luvéa Beauty adalah aplikasi katalog dan pemesanan produk kecantikan untuk membantu kamu menemukan beauty essentials favorit.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Ink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Versi 1.0", style = MaterialTheme.typography.labelMedium, color = Muted)
        }
    }
}
