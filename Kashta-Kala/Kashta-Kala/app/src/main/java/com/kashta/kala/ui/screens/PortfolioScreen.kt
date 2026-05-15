package com.kashta.kala.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashta.kala.data.model.PortfolioItem
import com.kashta.kala.ui.theme.*
import com.kashta.kala.viewmodel.PortfolioViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(viewModel: PortfolioViewModel = viewModel()) {
    val items by viewModel.portfolioItems.collectAsState(initial = emptyList())
    val showDialog by viewModel.showAddDialog.collectAsState()
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, "Add Work")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(SecondaryBrown.copy(alpha = 0.15f), MaterialTheme.colorScheme.background)))
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
            ) {
                Column {
                    Text("🖼️ My Portfolio", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text("Showcase your completed work", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text("${items.size} completed works", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp))

            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪵", fontSize = 64.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("No work added yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Tap + to showcase your craftsmanship!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item -> PortfolioCard(item, fmt) }
                }
            }
        }
    }

    // Add Dialog
    if (showDialog) {
        AddPortfolioDialog(viewModel)
    }
}

@Composable
private fun PortfolioCard(item: PortfolioItem, fmt: NumberFormat) {
    val cc = when (item.category) { "Sofa"->SofaColor; "Bed"->BedColor; "Cabinet"->CabinetColor; "Table"->TableColor; "Chair"->ChairColor; else->PrimaryAmber }
    Card(modifier = Modifier.fillMaxWidth().animateContentSize(), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp).background(Brush.verticalGradient(listOf(cc.copy(alpha = 0.2f), cc.copy(alpha = 0.08f)))), contentAlignment = Alignment.Center) {
                Text(item.imageEmoji, fontSize = 48.sp)
                Box(Modifier.align(Alignment.TopStart).padding(8.dp).clip(RoundedCornerShape(8.dp)).background(cc.copy(alpha = 0.15f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(item.category, style = MaterialTheme.typography.labelSmall, color = cc, fontWeight = FontWeight.Bold)
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(item.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text("👤 ${item.customerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("🪵 ${item.woodType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                if (item.price > 0) {
                    Text(fmt.format(item.price), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPortfolioDialog(viewModel: PortfolioViewModel) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val category by viewModel.category.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val woodType by viewModel.woodType.collectAsState()
    val price by viewModel.price.collectAsState()

    var catExpanded by remember { mutableStateOf(false) }
    var woodExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { viewModel.hideDialog() },
        title = { Text("Add Completed Work", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { viewModel.updateTitle(it) }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = description, onValueChange = { viewModel.updateDescription(it) }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), maxLines = 2)

                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                    OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("Category") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(catExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        viewModel.categories.forEach { c -> DropdownMenuItem(text = { Text(c) }, onClick = { viewModel.updateCategory(c); catExpanded = false }) }
                    }
                }

                ExposedDropdownMenuBox(expanded = woodExpanded, onExpandedChange = { woodExpanded = !woodExpanded }) {
                    OutlinedTextField(value = woodType, onValueChange = {}, readOnly = true, label = { Text("Wood Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(woodExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor())
                    ExposedDropdownMenu(expanded = woodExpanded, onDismissRequest = { woodExpanded = false }) {
                        viewModel.woodTypes.forEach { w -> DropdownMenuItem(text = { Text(w) }, onClick = { viewModel.updateWoodType(w); woodExpanded = false }) }
                    }
                }

                OutlinedTextField(value = customerName, onValueChange = { viewModel.updateCustomerName(it) }, label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = price, onValueChange = { viewModel.updatePrice(it) }, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.addPortfolioItem() }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.hideDialog() }) { Text("Cancel") }
        }
    )
}
