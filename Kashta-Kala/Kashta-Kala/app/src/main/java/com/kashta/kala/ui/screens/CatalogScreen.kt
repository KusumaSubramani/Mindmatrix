package com.kashta.kala.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashta.kala.data.model.FurnitureDesign
import com.kashta.kala.ui.theme.*
import com.kashta.kala.viewmodel.CatalogViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(viewModel: CatalogViewModel = viewModel()) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val designs by viewModel.designs.collectAsState(initial = emptyList())
    var selectedDesign by remember { mutableStateOf<FurnitureDesign?>(null) }
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    MaterialTheme.colorScheme.background
                )))
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
        ) {
            Column {
                Text("Design Catalog", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text("Browse modern furniture designs", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Category chips
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewModel.categories.forEach { cat ->
                val sel = selectedCategory == cat
                val label = when (cat) { "All"->"🪵 All"; "Sofa"->"🛋️ Sofa"; "Bed"->"🛏️ Bed"; "Cabinet"->"🗄️ Cabinet"; "Table"->"🪑 Table"; "Chair"->"💺 Chair"; else->cat }
                FilterChip(selected = sel, onClick = { viewModel.selectCategory(cat) },
                    label = { Text(label, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), selectedLabelColor = MaterialTheme.colorScheme.primary))
            }
        }

        Text("${designs.size} designs found", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp))

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(designs) { design ->
                DesignCard(design, fmt, { viewModel.toggleFavorite(design.designId) }, { selectedDesign = design })
            }
        }
    }

    selectedDesign?.let { design ->
        ModalBottomSheet(onDismissRequest = { selectedDesign = null }, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            DesignDetailSheet(design, fmt)
        }
    }
}

@Composable
private fun DesignCard(design: FurnitureDesign, fmt: NumberFormat, onFav: () -> Unit, onClick: () -> Unit) {
    val cc = catColor(design.category)
    Card(modifier = Modifier.fillMaxWidth().animateContentSize().clickable { onClick() }, shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Brush.verticalGradient(listOf(cc.copy(alpha = 0.2f), cc.copy(alpha = 0.08f))))) {
                Box(modifier = Modifier.padding(8.dp).clip(RoundedCornerShape(8.dp)).background(cc.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(design.category, style = MaterialTheme.typography.labelSmall, color = cc, fontWeight = FontWeight.Bold)
                }
                Text(design.imageEmoji, fontSize = 48.sp, modifier = Modifier.align(Alignment.Center))
                IconButton(onClick = onFav, modifier = Modifier.align(Alignment.TopEnd).size(36.dp)) {
                    val fc by animateColorAsState(if (design.isFavorite) Color(0xFFE91E63) else Color.Gray, label = "fav")
                    Icon(if (design.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, "Favorite", tint = fc, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(design.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(design.woodType, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Filled.Star, null, tint = PrimaryAmber, modifier = Modifier.size(12.dp))
                    Text(" ${design.rating}", style = MaterialTheme.typography.labelSmall, color = PrimaryAmber)
                }
                Spacer(Modifier.height(4.dp))
                Text(fmt.format(design.estimatedPrice), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DesignDetailSheet(design: FurnitureDesign, fmt: NumberFormat) {
    val cc = catColor(design.category)
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(20.dp)).background(Brush.verticalGradient(listOf(cc.copy(alpha = 0.2f), cc.copy(alpha = 0.05f)))), contentAlignment = Alignment.Center) {
            Text(design.imageEmoji, fontSize = 72.sp)
        }
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(design.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Box(Modifier.clip(RoundedCornerShape(8.dp)).background(cc.copy(alpha = 0.15f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(design.category, style = MaterialTheme.typography.labelMedium, color = cc, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(design.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("🪵 ${design.woodType}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold); Text("Wood", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("📐 ${design.dimensions}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold); Text("Size", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("⭐ ${design.rating}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold); Text("Rating", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        Spacer(Modifier.height(20.dp))
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))) {
            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Estimated Price", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(fmt.format(design.estimatedPrice), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

private fun catColor(cat: String): Color = when (cat) { "Sofa"->SofaColor; "Bed"->BedColor; "Cabinet"->CabinetColor; "Table"->TableColor; "Chair"->ChairColor; else->PrimaryAmber }
