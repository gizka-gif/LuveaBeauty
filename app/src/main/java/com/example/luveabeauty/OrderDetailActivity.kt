package com.example.luveabeauty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.data.AppDatabase
import com.example.luveabeauty.data.OrderEntity
import com.example.luveabeauty.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLuveaEdgeToEdge()

        // Menerima data dari MainActivity melalui Explicit Intent
        val orderNumber = intent.getStringExtra("ORDER_NUMBER") ?: "-"
        val userName = intent.getStringExtra("USER_NAME") ?: "Beauty Customer"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: "-"
        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            LuvéaBeautyTheme {
                val ordersFlow = remember { database.orderDao().getAllOrders() }
                val allOrders by ordersFlow.collectAsState(initial = emptyList())
                val items = allOrders.filter { it.orderNumber == orderNumber }

                OrderDetailScreen(
                    orderNumber = orderNumber,
                    userName = userName,
                    userEmail = userEmail,
                    items = items,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun OrderDetailScreen(
    orderNumber: String,
    userName: String,
    userEmail: String,
    items: List<OrderEntity>,
    onBack: () -> Unit
) {
    Surface(Modifier.fillMaxSize(), color = Cream) {
        Column(Modifier.safeDrawingPadding()) {
            ScreenHeader(title = "Detail pesanan", onBack = onBack)

            if (items.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.Inventory2,
                    title = "Pesanan tidak ditemukan",
                    subtitle = "Pesanan $orderNumber mungkin sudah dihapus.",
                    modifier = Modifier.weight(1f)
                )
            } else {
                val first = items.first()
                val total = items.sumOf { it.totalPrice }
                val dateText = remember(first.orderDate) {
                    SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date(first.orderDate))
                }

                Column(
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    LuveaCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Nomor pesanan", style = MaterialTheme.typography.labelMedium, color = Muted)
                                Text(orderNumber, style = MaterialTheme.typography.titleMedium, color = Raspberry)
                                Text(dateText, style = MaterialTheme.typography.bodySmall, color = Muted)
                            }
                            StatusChip(first.status)
                        }
                    }

                    OrderTimeline(status = first.status, isCod = first.paymentMethod == "COD")

                    SectionCard("Pelanggan") {
                        InfoLine("Nama", userName)
                        Spacer(Modifier.height(8.dp))
                        InfoLine("Email", userEmail)
                    }

                    SectionCard("Pengiriman & pembayaran") {
                        InfoLine("Alamat", first.address)
                        Spacer(Modifier.height(8.dp))
                        InfoLine("Pembayaran", first.paymentMethod)
                    }

                    SectionCard("Produk dipesan") {
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
                                Text(rupiahText(item.totalPrice), style = MaterialTheme.typography.titleSmall, color = Ink)
                            }
                        }
                        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Line)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Total", style = MaterialTheme.typography.titleMedium, color = Ink, modifier = Modifier.weight(1f))
                            Text(rupiahText(total), style = MaterialTheme.typography.titleLarge, color = Rose)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            StickyBar {
                PrimaryButton("Kembali", onBack, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Muted)
        Text(value, style = MaterialTheme.typography.bodyLarge, color = Ink)
    }
}

@Composable
private fun OrderTimeline(status: String, isCod: Boolean) {
    if (status == "Dibatalkan") {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Danger.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Cancel, contentDescription = null, tint = Danger)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Pesanan dibatalkan", style = MaterialTheme.typography.titleSmall, color = Danger)
                Text("Stok produk sudah dikembalikan.", style = MaterialTheme.typography.bodySmall, color = Danger)
            }
        }
        return
    }

    val steps = if (isCod) listOf("Dikemas", "Dikirim", "Selesai")
    else listOf("Belum Bayar", "Dikemas", "Dikirim", "Selesai")
    val descriptions = mapOf(
        "Belum Bayar" to "Menunggu pembayaran",
        "Dikemas" to "Pesanan sedang dikemas",
        "Dikirim" to "Pesanan dalam perjalanan",
        "Selesai" to "Pesanan sudah diterima"
    )
    val current = steps.indexOf(status).coerceAtLeast(0)

    SectionCard("Status pesanan") {
        steps.forEachIndexed { i, step ->
            val done = i < current || (i == current && status == "Selesai")
            val active = i == current && !done
            Row(Modifier.height(IntrinsicSize.Min)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (done) Rose else if (active) Color.White else Line)
                            .then(if (active) Modifier.border(2.dp, Rose, CircleShape) else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        if (done) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                        } else if (active) {
                            Box(Modifier.size(10.dp).clip(CircleShape).background(Rose))
                        }
                    }
                    if (i < steps.lastIndex) {
                        Box(
                            Modifier
                                .width(2.dp)
                                .weight(1f)
                                .background(if (i < current) Rose else Line)
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.padding(bottom = if (i < steps.lastIndex) 18.dp else 0.dp)) {
                    Text(
                        step,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (done || active) Ink else Muted
                    )
                    Text(descriptions[step] ?: "", style = MaterialTheme.typography.bodySmall, color = Muted)
                }
            }
        }
    }
}
