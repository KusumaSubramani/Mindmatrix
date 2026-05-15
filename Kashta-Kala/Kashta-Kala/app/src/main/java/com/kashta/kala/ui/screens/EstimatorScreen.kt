package com.kashta.kala.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kashta.kala.ui.theme.*
import com.kashta.kala.viewmodel.EstimatorViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstimatorScreen(viewModel: EstimatorViewModel = viewModel()) {
    val furnitureType by viewModel.furnitureType.collectAsState()
    val woodType by viewModel.woodType.collectAsState()
    val length by viewModel.length.collectAsState()
    val width by viewModel.width.collectAsState()
    val height by viewModel.height.collectAsState()
    val customerName by viewModel.customerName.collectAsState()
    val estimate by viewModel.estimate.collectAsState()
    val quote by viewModel.quote.collectAsState()
    val showEstimate by viewModel.showEstimate.collectAsState()
    val showQuote by viewModel.showQuote.collectAsState()
    val fmt = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    var furnitureExpanded by remember { mutableStateOf(false) }
    var woodExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(TertiaryGreen.copy(alpha = 0.15f), MaterialTheme.colorScheme.background)))
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp)
        ) {
            Column {
                Text("📐 Material Estimator", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text("Calculate wood & generate quotes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Furniture Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                // Furniture Type Dropdown
                ExposedDropdownMenuBox(expanded = furnitureExpanded, onExpandedChange = { furnitureExpanded = !furnitureExpanded }) {
                    OutlinedTextField(
                        value = furnitureType, onValueChange = {}, readOnly = true,
                        label = { Text("Furniture Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = furnitureExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = furnitureExpanded, onDismissRequest = { furnitureExpanded = false }) {
                        viewModel.furnitureTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { viewModel.updateFurnitureType(type); furnitureExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Wood Type Dropdown
                ExposedDropdownMenuBox(expanded = woodExpanded, onExpandedChange = { woodExpanded = !woodExpanded }) {
                    OutlinedTextField(
                        value = "$woodType — ${fmt.format(viewModel.woodPrices[woodType] ?: 0)}/cft",
                        onValueChange = {}, readOnly = true,
                        label = { Text("Wood Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = woodExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = woodExpanded, onDismissRequest = { woodExpanded = false }) {
                        viewModel.woodTypes.forEach { type ->
                            val price = viewModel.woodPrices[type] ?: 0.0
                            DropdownMenuItem(text = { Text("$type — ${fmt.format(price)}/cft") }, onClick = { viewModel.updateWoodType(type); woodExpanded = false })
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Dimensions (in feet)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                // Dimension inputs
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = length, onValueChange = { viewModel.updateLength(it) }, label = { Text("Length") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = width, onValueChange = { viewModel.updateWidth(it) }, label = { Text("Width") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = height, onValueChange = { viewModel.updateHeight(it) }, label = { Text("Height") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f), singleLine = true)
                }

                Spacer(Modifier.height(16.dp))

                // Customer name
                OutlinedTextField(value = customerName, onValueChange = { viewModel.updateCustomerName(it) }, label = { Text("Customer Name (optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                Spacer(Modifier.height(20.dp))

                // Buttons
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.calculateEstimate() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = TertiaryGreen)
                    ) {
                        Icon(Icons.Filled.Calculate, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Calculate")
                    }
                    OutlinedButton(onClick = { viewModel.clearResults() }, modifier = Modifier.weight(0.5f)) {
                        Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Clear")
                    }
                }
            }
        }

        // Estimate Result
        AnimatedVisibility(visible = showEstimate, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            estimate?.let { est ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = TertiaryGreen.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("📊 Estimation Result", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TertiaryGreen)
                        Spacer(Modifier.height(12.dp))
                        EstimateRow("📏 Area (L × W)", "${est.length} × ${est.width} = ${"%.2f".format(est.area)} sq ft")
                        EstimateRow("📦 Volume (L × W × H)", "${est.length} × ${est.width} × ${est.height} = ${"%.2f".format(est.volume)} cu ft")
                        EstimateRow("♻️ Wastage", "${est.wastagePercent.toInt()}%")
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        EstimateRow("🪵 Wood Required", "${"%.2f".format(est.woodRequired)} cubic feet")

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.generateQuote() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAmber)
                        ) {
                            Icon(Icons.Filled.Receipt, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Generate Price Quote")
                        }
                    }
                }
            }
        }

        // Quote Result
        AnimatedVisibility(visible = showQuote, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            quote?.let { q ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryAmber.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("💰 Price Quotation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryAmber)
                        Spacer(Modifier.height(4.dp))
                        Text("For: ${q.customerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        EstimateRow("🪵 Material Cost", fmt.format(q.materialCost))
                        EstimateRow("👷 Labor Cost (30%)", fmt.format(q.laborCost))
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                            Text(fmt.format(q.totalCost), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = PrimaryAmber)
                        }
                        Spacer(Modifier.height(12.dp))
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                            Column(Modifier.padding(12.dp)) {
                                Text("✍️ AI Quote Description", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(4.dp))
                                Text(q.description, style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun EstimateRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
