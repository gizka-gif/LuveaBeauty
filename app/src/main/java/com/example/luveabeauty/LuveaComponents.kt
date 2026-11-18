package com.example.luveabeauty

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.luveabeauty.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

// ---------------------------------------------------------------- Data & helper

data class BeautyProduct(
    val id: Int,
    val name: String,
    val category: String,
    val price: Int,
    val emoji: String,
    val description: String,
    val rating: Double,
    val soldCount: Int,
    val stock: Int
) : java.io.Serializable

fun rupiah(value: Int): String {
    return NumberFormat.getNumberInstance(Locale("id", "ID")).format(value)
}

fun rupiahText(value: Int): String = "Rp ${rupiah(value)}"

fun getProductImageByName(name: String): Int {
    return when (name) {
        "Blush On Pink" -> R.drawable.blush_on
        "Body Lotion" -> R.drawable.body_serum
        "Glow Serum" -> R.drawable.glow_serum
        "Hydrating Toner" -> R.drawable.hydrating_toner
        "Soft Matte Lipstick" -> R.drawable.lipstick
        "Bronzer" -> R.drawable.bronzer
        "Foundation" -> R.drawable.foundation
        "Eyeshadow" -> R.drawable.eyeshadow
        "Mascara" -> R.drawable.mascara
        "Sunscreen" -> R.drawable.sunscreen
        "Moisturizer" -> R.drawable.moisturizer
        "Cleansing Oil" -> R.drawable.cleansing_oil
        "Masker Wajah" -> R.drawable.masker_wajah
        "Body Wash" -> R.drawable.body_wash
        "Body Scrub" -> R.drawable.body_scrub
        else -> R.drawable.blush_on
    }
}

fun getProductImage(product: BeautyProduct): Int = getProductImageByName(product.name)

fun statusColor(status: String): Color = when (status) {
    "Belum Bayar" -> Warning
    "Dikemas" -> Rose
    "Dikirim" -> Info
    "Selesai" -> Success
    "Dibatalkan" -> Danger
    else -> Muted
}

// ---------------------------------------------------------------- Tombol & kartu

@Composable
fun RoundIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = Ink,
    container: Color = Color.White,
    size: Dp = 42.dp
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = container,
        border = BorderStroke(1.dp, Line)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = description, tint = tint, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
fun CartAction(count: Int, onClick: () -> Unit) {
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(containerColor = Rose, contentColor = Color.White) {
                    Text(if (count > 99) "99+" else "$count")
                }
            }
        }
    ) {
        RoundIconButton(
            icon = Icons.Rounded.ShoppingBag,
            description = "Keranjang",
            onClick = onClick
        )
    }
}

@Composable
fun LuveaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val colors = CardDefaults.cardColors(containerColor = Color.White)
    val border = BorderStroke(1.dp, Line)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, border = border, content = content)
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Rose,
            contentColor = Color.White,
            disabledContainerColor = Rose.copy(alpha = 0.35f),
            disabledContentColor = Color.White
        )
    ) {
        if (loading) {
            CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    color: Color = Rose
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (enabled) color else color.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, maxLines = 1)
    }
}

// Bar aksi yang menempel di bawah halaman (tombol utama selalu terjangkau)
@Composable
fun StickyBar(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = Color.White, shadowElevation = 16.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            content = content
        )
    }
}

// ---------------------------------------------------------------- Header & teks

@Composable
fun ScreenHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            RoundIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                description = "Kembali",
                onClick = onBack
            )
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        actions()
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
            modifier = Modifier.weight(1f)
        )
        if (actionText != null) {
            TextButton(onClick = onAction) { Text(actionText, color = Rose) }
        }
    }
}

@Composable
fun RatingRow(rating: Double, sold: Int, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(3.dp))
        Text("$rating", style = MaterialTheme.typography.labelMedium, color = Ink)
        Text("  •  $sold terjual", style = MaterialTheme.typography.labelMedium, color = Muted)
    }
}

@Composable
fun StatusChip(status: String, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = color, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Blush),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Rose, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = Ink, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Muted, textAlign = TextAlign.Center)
        if (actionText != null) {
            Spacer(Modifier.height(22.dp))
            PrimaryButton(actionText, onAction, Modifier.fillMaxWidth(0.7f))
        }
    }
}

// ---------------------------------------------------------------- Input

@Composable
fun luveaFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Rose,
    unfocusedBorderColor = FieldBorder,
    errorBorderColor = Danger,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White,
    errorContainerColor = Color.White,
    focusedLabelColor = Rose,
    cursorColor = Rose
)

@Composable
fun LuveaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    errorText: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String? = null
) {
    val focus = LocalFocusManager.current
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = Muted) } },
        trailingIcon = trailingIcon,
        isError = errorText != null,
        supportingText = errorText?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = minLines,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { focus.moveFocus(FocusDirection.Down) },
            onDone = {
                focus.clearFocus()
                onImeAction?.invoke()
            }
        ),
        shape = RoundedCornerShape(16.dp),
        colors = luveaFieldColors()
    )
}

// ---------------------------------------------------------------- Produk

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(36.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                tint = if (isFavorite) Rose else Muted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProductTile(
    product: BeautyProduct,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LuveaCard(modifier = modifier, onClick = onClick) {
        Box {
            Image(
                painter = painterResource(id = getProductImage(product)),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            FavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
            if (product.stock <= 0) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Ink.copy(alpha = 0.8f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Stok habis", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(product.category, style = MaterialTheme.typography.labelSmall, color = Muted)
            Spacer(Modifier.height(2.dp))
            Text(
                product.name,
                style = MaterialTheme.typography.titleSmall,
                color = Ink,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(rupiahText(product.price), style = MaterialTheme.typography.titleSmall, color = Rose)
            Spacer(Modifier.height(4.dp))
            RatingRow(product.rating, product.soldCount)
        }
    }
}

@Composable
fun QuantityStepper(quantity: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RoundIconButton(Icons.Rounded.Remove, "Kurangi", onMinus, size = 32.dp, tint = Rose)
        Text(
            "$quantity",
            style = MaterialTheme.typography.titleMedium,
            color = Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 36.dp)
        )
        RoundIconButton(Icons.Rounded.Add, "Tambah", onPlus, size = 32.dp, tint = Rose)
    }
}

@Composable
fun LuveaChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier
            .clip(shape)
            .background(if (selected) Rose else Color.White)
            .border(1.dp, if (selected) Rose else FieldBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) Color.White else Ink,
            maxLines = 1
        )
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null
) {
    val focus = LocalFocusManager.current
    val base = modifier.fillMaxWidth()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (focusRequester != null) base.focusRequester(focusRequester) else base,
        singleLine = true,
        placeholder = { Text("Cari produk kecantikan") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Muted) },
        trailingIcon = if (value.isNotEmpty()) {
            {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Hapus pencarian", tint = Muted)
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() }),
        shape = RoundedCornerShape(16.dp),
        colors = luveaFieldColors()
    )
}
