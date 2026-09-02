package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ExpenseTemplateEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ExpenseSubTab {
    TEMPLATES,
    PLANNER,
    LOG
}

@Composable
fun ExpensesScreen(
    templates: List<ExpenseTemplateEntity>,
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    onCreateTemplateClick: () -> Unit,
    onEditTemplateClick: (ExpenseTemplateEntity) -> Unit,
    onQuickLogTemplate: (ExpenseTemplateEntity) -> Unit,
    onTogglePlanned: (ExpenseTemplateEntity) -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ExpenseSubTab.TEMPLATES) }
    var searchQuery by remember { mutableStateOf("") }

    val expenseTxs = remember(transactions) {
        transactions.filter { it.type == "EXPENSE" }
    }

    val totalSpentThisMonth = remember(expenseTxs) {
        expenseTxs.sumOf { it.amount }
    }

    val totalPlannedAmount = remember(templates) {
        templates.filter { it.isPlanned }.sumOf { it.amount }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Bar Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXPENSE MANAGEMENT",
                    color = Slate400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Templates & Plan",
                    color = Slate50,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GlassButton(
                    text = "+ Template",
                    onClick = onCreateTemplateClick,
                    modifier = Modifier.testTag("create_template_btn")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top Summary Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Monthly Spent
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentRose.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "MONTH SPENT", color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", totalSpentThisMonth)}",
                        color = AccentRose,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Card 2: Planned Monthly
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentCyan.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "PLANNED BASE", color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", totalPlannedAmount)}",
                        color = AccentCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Card 3: Active Templates
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentIndigo.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "TEMPLATES", color = Slate400, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${templates.size} Active",
                        color = AccentIndigo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sub Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(GlassSurfaceDark)
                .border(1.dp, GlassBorderDark, RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                ExpenseSubTab.TEMPLATES to "Presets",
                ExpenseSubTab.PLANNER to "Monthly Plan",
                ExpenseSubTab.LOG to "History Log"
            )

            tabs.forEach { (tab, label) ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AccentCyan else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp)
                        .testTag("expense_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.Black else Slate300,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedTab) {
            ExpenseSubTab.TEMPLATES -> {
                // Templates Grid / List
                val filteredTemplates = remember(templates, searchQuery) {
                    if (searchQuery.isBlank()) templates
                    else templates.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.category.contains(searchQuery, ignoreCase = true)
                    }
                }

                if (filteredTemplates.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "No Expense Templates Found", color = Slate300, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap '+ Template' above to create custom recurring expense presets", color = Slate400, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTemplates, key = { it.id }) { template ->
                            ExpenseTemplateCardItem(
                                template = template,
                                currencySymbol = currencySymbol,
                                onQuickLog = { onQuickLogTemplate(template) },
                                onTogglePlanned = { onTogglePlanned(template) },
                                onEdit = { onEditTemplateClick(template) }
                            )
                        }
                    }
                }
            }

            ExpenseSubTab.PLANNER -> {
                // Monthly Planner view
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderGlowColor = AccentCyan.copy(alpha = 0.25f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "MONTHLY EXPENSE PLANNER",
                                    color = Slate400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Planned vs Actual Spend",
                                    color = Slate50,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                val ratio = if (totalPlannedAmount > 0) (totalSpentThisMonth / totalPlannedAmount).coerceIn(0.0, 1.0).toFloat() else 0f
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (ratio > 0.9f) AccentRose else AccentCyan,
                                    trackColor = Slate800
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Spent: $currencySymbol${String.format(Locale.US, "%.2f", totalSpentThisMonth)}",
                                        color = Slate300,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Target Plan: $currencySymbol${String.format(Locale.US, "%.2f", totalPlannedAmount)}",
                                        color = AccentCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "PLANNED EXPENSE TEMPLATES",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    val plannedTemplates = templates.filter { it.isPlanned }
                    if (plannedTemplates.isEmpty()) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "No templates flagged for this month's plan. Tap 'Plan' on any template card to add it.",
                                    color = Slate400,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(plannedTemplates, key = { it.id }) { template ->
                            val matchedSpent = expenseTxs
                                .filter { it.category.equals(template.category, ignoreCase = true) }
                                .sumOf { it.amount }

                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderGlowColor = AccentCyan.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AccentCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = getTemplateIcon(template.iconName),
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = template.title, color = Slate50, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Plan: $currencySymbol${String.format(Locale.US, "%.2f", template.amount)} · Spent in ${template.category}: $currencySymbol${String.format(Locale.US, "%.2f", matchedSpent)}", color = Slate400, fontSize = 11.sp)
                                    }

                                    IconButton(onClick = { onQuickLogTemplate(template) }) {
                                        Icon(imageVector = Icons.Default.Bolt, contentDescription = "Log Expense", tint = AccentEmerald)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ExpenseSubTab.LOG -> {
                // Expense History Log List
                Column(modifier = Modifier.fillMaxSize()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search expense transactions...", color = Slate400, fontSize = 13.sp) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Slate400) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GlassSurfaceDark,
                            unfocusedContainerColor = GlassSurfaceDark,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = GlassBorderDark,
                            focusedTextColor = Slate50
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val filteredTxs = remember(expenseTxs, searchQuery) {
                        if (searchQuery.isBlank()) expenseTxs
                        else expenseTxs.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTxs, key = { it.id }) { tx ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTransactionClick(tx) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val catMeta = CategoryRegistry.getCategory(tx.category)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(catMeta.color.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = catMeta.icon,
                                            contentDescription = null,
                                            tint = catMeta.color,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = tx.title, color = Slate50, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "${tx.category} · ${dateFormat.format(Date(tx.dateMillis))}", color = Slate400, fontSize = 11.sp)
                                    }

                                    Text(
                                        text = "-$currencySymbol${String.format(Locale.US, "%.2f", tx.amount)}",
                                        color = AccentRose,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseTemplateCardItem(
    template: ExpenseTemplateEntity,
    currencySymbol: String,
    onQuickLog: () -> Unit,
    onTogglePlanned: () -> Unit,
    onEdit: () -> Unit
) {
    val catMeta = CategoryRegistry.getCategory(template.category)

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderGlowColor = if (template.isPlanned) AccentCyan.copy(alpha = 0.35f) else GlassBorderDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(catMeta.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTemplateIcon(template.iconName),
                            contentDescription = null,
                            tint = catMeta.color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = template.title,
                            color = Slate50,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ShadcnBadge(text = template.category, variant = BadgeVariant.SECONDARY)
                            ShadcnBadge(text = template.frequency, variant = BadgeVariant.CYAN)
                        }
                    }
                }

                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%.2f", template.amount)}",
                    color = AccentCyan,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (template.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = template.note,
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Planned status chip button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (template.isPlanned) AccentCyan.copy(alpha = 0.15f) else GlassSurfaceDark)
                        .border(1.dp, if (template.isPlanned) AccentCyan.copy(alpha = 0.4f) else GlassBorderDark, RoundedCornerShape(10.dp))
                        .clickable { onTogglePlanned() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (template.isPlanned) Icons.Default.Check else Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = if (template.isPlanned) AccentCyan else Slate400,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (template.isPlanned) "Planned" else "Unplanned",
                            color = if (template.isPlanned) AccentCyan else Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(GlassSurfaceDark)
                            .border(1.dp, GlassBorderDark, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Template",
                            tint = Slate300,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Quick 1-tap post button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AccentEmerald, AccentCyan)
                                )
                            )
                            .clickable { onQuickLog() }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Use Now",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getTemplateIcon(iconName: String): ImageVector {
    return when (iconName) {
        "ShoppingBag" -> Icons.Default.ShoppingBag
        "Fastfood" -> Icons.Default.Fastfood
        "LocalGasStation" -> Icons.Default.LocalGasStation
        "FitnessCenter" -> Icons.Default.FitnessCenter
        else -> Icons.Default.Receipt
    }
}
