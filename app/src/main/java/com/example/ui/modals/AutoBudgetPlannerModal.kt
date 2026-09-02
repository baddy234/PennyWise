package com.example.ui.modals

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AutoBudgetPlannerModal(
    totalFundBalance: Double,
    availableCategories: List<ExpenseCategory>,
    currencySymbol: String,
    onApplyAllocations: (Map<String, Double>) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var budgetInput by remember { mutableStateOf(totalFundBalance.takeIf { it > 0 }?.let { String.format(Locale.US, "%.0f", it) } ?: "2000") }
    var selectedCategoryNames by remember {
        mutableStateOf(availableCategories.map { it.name }.toSet())
    }

    // Weight allocations map: category -> allocated amount
    val allocations = remember { mutableStateMapOf<String, Double>() }
    var isPlanned by remember { mutableStateOf(false) }

    val totalBudget = budgetInput.toDoubleOrNull() ?: 0.0

    // Auto calculate allocations when triggered or input changes
    fun recalculateAutoAllocations() {
        if (totalBudget <= 0 || selectedCategoryNames.isEmpty()) {
            allocations.clear()
            return
        }

        val selectedCats = availableCategories.filter { it.name in selectedCategoryNames }

        // Category weighting priorities (Essential vs Lifestyle)
        val weightMap = mapOf(
            "Housing & Rent" to 3.0,
            "Groceries" to 2.5,
            "Transport" to 1.8,
            "Health & Fitness" to 1.5,
            "Shopping" to 1.0,
            "Entertainment" to 0.8,
            "Food & Dining" to 1.2,
            "Utilities & Bills" to 1.8,
            "Investments" to 1.5,
            "Education" to 1.2
        )

        val totalWeight = selectedCats.sumOf { weightMap[it.name] ?: 1.0 }

        allocations.clear()
        selectedCats.forEach { cat ->
            val catWeight = weightMap[cat.name] ?: 1.0
            val portion = (catWeight / totalWeight) * totalBudget
            allocations[cat.name] = (portion / 10).toInt() * 10.0 // Round to clean $10 increments
        }
        isPlanned = true
    }

    // Trigger initial calculation
    LaunchedEffect(Unit) {
        recalculateAutoAllocations()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Slate900,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Modal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AccentCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Auto Budget",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SMART PLANNER",
                            color = Slate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Auto-Plan Budget",
                            color = Slate50,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassSurfaceDark)
                        .border(1.dp, GlassBorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Slate300
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Input Monthly Budget / Fund Baseline
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Enter Monthly Budget Target",
                            color = Slate100,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (totalFundBalance > 0) {
                            Text(
                                text = "Fund Total: $currencySymbol${String.format(Locale.US, "%.0f", totalFundBalance)}",
                                color = AccentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AccentEmerald.copy(alpha = 0.15f))
                                    .clickable {
                                        budgetInput = String.format(Locale.US, "%.0f", totalFundBalance)
                                        recalculateAutoAllocations()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = {
                            budgetInput = it
                            isPlanned = false
                        },
                        prefix = { Text("$currencySymbol ", color = AccentCyan, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("0.00", color = Slate400) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate800,
                            unfocusedContainerColor = Slate800,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorderDark,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate50
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auto_budget_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 2: Select Included Expense Categories
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Select Expense Categories to Fund",
                        color = Slate100,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The algorithm prioritizes essentials like housing & food",
                        color = Slate400,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableCategories.forEach { category ->
                            val isSelected = category.name in selectedCategoryNames
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) AccentCyan.copy(alpha = 0.2f) else Slate800
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AccentCyan else GlassBorderDark,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedCategoryNames = if (isSelected) {
                                            selectedCategoryNames - category.name
                                        } else {
                                            selectedCategoryNames + category.name
                                        }
                                        isPlanned = false
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = category.name,
                                        color = if (isSelected) Slate50 else Slate400,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { recalculateAutoAllocations() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auto_plan_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Slate900,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate Smart Allocation Plan",
                            color = Slate900,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step 3: Allocated Category Results & Manual Adjustment Sliders
            AnimatedVisibility(
                visible = isPlanned && allocations.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column {
                    val currentAllocatedSum = allocations.values.sum()

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "3. Recommended Category Caps",
                                        color = Slate100,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Adjust sliders to tweak allocations",
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }

                                ShadcnBadge(
                                    text = "$currencySymbol${String.format(Locale.US, "%.0f", currentAllocatedSum)} / $currencySymbol${String.format(Locale.US, "%.0f", totalBudget)}",
                                    variant = if (currentAllocatedSum <= totalBudget) BadgeVariant.SUCCESS else BadgeVariant.WARNING
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            allocations.forEach { (categoryName, amount) ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = categoryName,
                                            color = Slate100,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "$currencySymbol${String.format(Locale.US, "%.0f", amount)}",
                                            color = AccentCyan,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Slider(
                                        value = amount.toFloat(),
                                        onValueChange = { newValue ->
                                            allocations[categoryName] = (newValue / 5).toInt() * 5.0
                                        },
                                        valueRange = 0f..(totalBudget.toFloat().coerceAtLeast(100f)),
                                        colors = SliderDefaults.colors(
                                            thumbColor = AccentCyan,
                                            activeTrackColor = AccentCyan,
                                            inactiveTrackColor = Slate800
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Apply Button
                    Button(
                        onClick = {
                            onApplyAllocations(allocations.toMap())
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("apply_planned_budget_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Slate900,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apply & Save Planned Budget",
                            color = Slate900,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

