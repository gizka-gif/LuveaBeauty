package com.example.luveabeauty

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.data.CartEntity
import com.example.luveabeauty.ui.theme.*

// ================================================================ KERANJANG

@Composable
fun CartPage(
    cart: List<CartEntity>,
    onBack: () -> Unit,
    onCheckout: () -> Unit,
    onRemove: (CartEntity) -> Unit,
    onQuantityChange: (CartEntity, Int) -> Unit,
    onShop: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Keranjang", onBack = onBack)

        if (cart.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.ShoppingBag,
                title = "Keranjangmu masih kosong",
                subtitle = "Pilih produk favoritmu dan tambahkan ke keranjang.",
                modifier = Modifier.weight(1f),
                actionText = "Mulai belanja",
                onAction = onShop
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cart, key = { it.id }) { item ->
                    LuveaCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp)) {
                            Image(
                                painter = painterResource(id = getProductImageByName(item.productName)),
                                contentDescription = item.productName,
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Column(Modifier.weight(1f)) {
                                        Text(item.productName, style = MaterialTheme.typography.titleSmall, color = Ink)
                                        Text(item.category, style = MaterialTheme.typography.labelSmall, color = Muted)
                                    }
                                    IconButton(onClick = { onRemove(item) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Hapus", tint = Danger, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(rupiahText(item.price), style = MaterialTheme.typography.bodySmall, color = Muted)
                                Spacer(Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    QuantityStepper(
                                        quantity = item.quantity,
                                        onMinus = { onQuantityChange(item, item.quantity - 1) },
                                        onPlus = { onQuantityChange(item, item.quantity + 1) }
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        rupiahText(item.price * item.quantity),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Rose
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val total = cart.sumOf { it.price * it.quantity }
            val count = cart.sumOf { it.quantity }
            StickyBar {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Total ($count produk)", style = MaterialTheme.typography.labelMedium, color = Muted)
                        Text(rupiahText(total), style = MaterialTheme.typography.titleLarge, color = Rose)
                    }
                    PrimaryButton("Checkout", onCheckout, Modifier.weight(1f))
                }
            }
        }
    }
}

// ================================================================ CHECKOUT

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    LuveaCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun PaymentOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) BlushSoft else Color.White)
            .border(1.5.dp, if (selected) Rose else Line, shape)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Blush),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Rose, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Muted)
        }
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Rose)
        )
    }
}

@Composable
fun CheckoutPage(
    items: List<CartEntity>,
    recipient: String,
    phone: String,
    address: String,
    paymentMethod: String,
    placing: Boolean,
    onRecipientChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPaymentChange: (String) -> Unit,
    onPlaceOrder: () -> Unit,
    onBack: () -> Unit
) {
    var recipientError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }
    val total = items.sumOf { it.price * it.quantity }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Checkout", onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionCard("Alamat pengiriman") {
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

            SectionCard("Pesananmu") {
                items.forEachIndexed { index, item ->
                    if (index > 0) HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Line)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = getProductImageByName(item.productName)),
                            contentDescription = item.productName,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.productName, style = MaterialTheme.typography.titleSmall, color = Ink)
                            Text("${item.quantity} x ${rupiahText(item.price)}", style = MaterialTheme.typography.bodySmall, color = Muted)
                        }
                        Text(rupiahText(item.price * item.quantity), style = MaterialTheme.typography.titleSmall, color = Ink)
                    }
                }
            }

            SectionCard("Metode pembayaran") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PaymentOption("Transfer Bank", "Virtual account, bayar setelah pesan", Icons.Rounded.AccountBalance, paymentMethod == "Transfer Bank") { onPaymentChange("Transfer Bank") }
                    PaymentOption("E-wallet", "Bayar lewat dompet digital", Icons.Rounded.AccountBalanceWallet, paymentMethod == "E-wallet") { onPaymentChange("E-wallet") }
                    PaymentOption("COD", "Bayar saat pesanan tiba", Icons.Rounded.Payments, paymentMethod == "COD") { onPaymentChange("COD") }
                }
            }
            Spacer(Modifier.height(4.dp))
        }

        StickyBar {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Total pembayaran", style = MaterialTheme.typography.labelMedium, color = Muted)
                    Text(rupiahText(total), style = MaterialTheme.typography.titleLarge, color = Rose)
                }
                PrimaryButton(
                    text = "Buat pesanan",
                    onClick = {
                        recipientError = recipient.isBlank()
                        phoneError = phone.count { it.isDigit() } !in 10..15
                        addressError = address.trim().length < 10
                        if (items.isNotEmpty() && !recipientError && !phoneError && !addressError) {
                            onPlaceOrder()
                        }
                    },
                    enabled = items.isNotEmpty(),
                    loading = placing,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ================================================================ PEMBAYARAN

@Composable
fun PaymentPage(
    method: String,
    orderNumber: String,
    total: Int,
    processing: Boolean,
    onPaid: () -> Unit,
    onLater: () -> Unit,
    onBack: () -> Unit
) {
    val steps = if (method == "Transfer Bank") {
        listOf(
            "Buka aplikasi m-banking atau ATM",
            "Pilih menu transfer ke virtual account",
            "Masukkan nomor virtual account di atas dan konfirmasi"
        )
    } else {
        listOf(
            "Buka aplikasi e-wallet kamu",
            "Pindai atau masukkan kode pembayaran",
            "Konfirmasi pembayaran sesuai total"
        )
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title = "Pembayaran", onBack = onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Raspberry, Rose)))
                    .padding(22.dp)
            ) {
                Text("Total pembayaran", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                Spacer(Modifier.height(4.dp))
                Text(rupiahText(total), style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Spacer(Modifier.height(14.dp))
                Text("Nomor pesanan", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
                Text(orderNumber, style = MaterialTheme.typography.titleSmall, color = Color.White)
                Spacer(Modifier.height(6.dp))
                Text("Metode: $method", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.75f))
            }

            if (method == "Transfer Bank") {
                LuveaCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Nomor virtual account", style = MaterialTheme.typography.labelMedium, color = Muted)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "8808 1234 5678 9012",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Raspberry
                        )
                    }
                }
            }

            SectionCard("Cara membayar") {
                steps.forEachIndexed { index, step ->
                    Row(Modifier.padding(bottom = if (index < steps.lastIndex) 12.dp else 0.dp)) {
                        Box(
                            Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Blush),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", style = MaterialTheme.typography.labelMedium, color = RoseDeep)
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(step, style = MaterialTheme.typography.bodyMedium, color = Ink, modifier = Modifier.weight(1f))
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BlushSoft)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Info, contentDescription = null, tint = Rose, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    "Pembayaran ini hanya simulasi, bukan transaksi sungguhan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoseDeep
                )
            }
            Spacer(Modifier.height(4.dp))
        }

        StickyBar {
            PrimaryButton("Bayar sekarang", onPaid, Modifier.fillMaxWidth(), loading = processing)
            TextButton(
                onClick = onLater,
                enabled = !processing,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Bayar nanti", color = Muted)
            }
        }
    }
}

// ================================================================ SUKSES

@Composable
fun SuccessPage(
    orderNumber: String,
    onOrders: () -> Unit,
    onHome: () -> Unit
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }
    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "successScale"
    )
    val fade by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(450),
        label = "successFade"
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(124.dp)
                .scale(scale)
                .alpha(fade)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Raspberry, Rose))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
        }
        Spacer(Modifier.height(26.dp))
        Text("Pesanan berhasil!", style = MaterialTheme.typography.headlineMedium, color = Raspberry)
        Spacer(Modifier.height(6.dp))
        Text(
            "Terima kasih sudah berbelanja di Luvéa Beauty.",
            style = MaterialTheme.typography.bodyMedium,
            color = Muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        LuveaCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Nomor pesanan", style = MaterialTheme.typography.labelMedium, color = Muted)
                Text(orderNumber, style = MaterialTheme.typography.titleMedium, color = Ink, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(28.dp))
        PrimaryButton("Lihat pesanan saya", onOrders, Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        SecondaryButton("Lanjut belanja", onHome, Modifier.fillMaxWidth())
    }
}
