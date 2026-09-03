package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.BadgeVariant
import com.example.ui.components.BudgetLimitProgressCard
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*
import java.util.Calendar
import java.util.Locale

import androidx.compose.material.icons.filled.AutoAwesome
import com.example.ui.modals.AutoBudgetPlannerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetLimitsScreen(
    limits: List<BudgetLimitEntity>,
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    totalFundBalance: Double = 0.0,
    onAddLimitClick: () -> Unit,
    onEditLimitClick: (BudgetLimitEntity) -> Unit,
    onToggleLimit: (BudgetLimitEntity) -> Unit,
    onApplyAutoBudget: (Map<String, Double>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance()
    val now = cal.timeInMillis

    // Start of Today
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startOfToday = cal.timeInMillis

    // Start of Week
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    val startOfWeek = cal.timeInMillis

    // Start of Month
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val startOfMonth = cal.timeInMillis

    // Start of Year
    cal.set(Calendar.DAY_OF_YEAR, 1)
    val startOfYear = cal.timeInMillis

    val expenseList = transactions.filter { it.type == "EXPENSE" }
    val dailySpend = expenseList.filter { it.dateMillis >= startOfToday }.sumOf { it.amount }
    val weeklySpend = expenseList.filter { it.dateMillis >= startOfWeek }.sumOf { it.amount }
    val monthlySpend = expenseList.filter { it.dateMillis >= startOfMonth }.sumOf { it.amount }
    val yearlySpend = expenseList.filter { it.dateMillis >= startOfYear }.sumOf { it.amount }

    val periodLimits = limits.filter { it.periodType != "CATEGORY" }
    val categoryLimits = limits.filter { it.periodType == "CATEGORY" }
    val activeLimitsCount = limits.count { it.isEnabled }

    var showInfoModal by remember { mutableStateOf(false) }
    var showAutoPlannerModal by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Clean, Minimal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Budget Ceilings",
                            color = Slate50,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (activeLimitsCount > 0) AccentEmerald else Slate500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$activeLimitsCount of ${limits.size} Caps Active",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    ShadcnBadge(
                        text = "Thresholds",
                        variant = BadgeVariant.WARNING
                    )
                }
            }

            // Main Content Action Bar: New Limit Button & Policy Rules Trigger
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add Limit Button
                    GlassButton(
                        text = "New Ceiling Limit",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = onAddLimitClick,
                        gradient = Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_limit_header_button")
                    )

                    // Alert Rules & Policy Modal Trigger
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900.copy(alpha = 0.85f))
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .clickable { showInfoModal = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("limits_policy_info_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Budget Policy & Alerts",
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Rules & Alerts",
                            color = Slate200,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Auto-Budget Planner Hero Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAutoPlannerModal = true }
                        .testTag("open_auto_planner_card"),
                    borderGlowColor = AccentCyan,
                    backgroundColor = Color(0x3306B6D4)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Auto Plan",
                                tint = AccentCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Auto-Plan Expenses",
                                    color = Slate50,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                ShadcnBadge(text = "Smart", variant = BadgeVariant.CYAN)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Enter income or budget & auto-allocate categories",
                                color = Slate300,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Notification Info Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x221E1B4B)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentIndigo.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automatic Limit Warnings",
                            color = Slate50,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "You'll receive an instant device notification when any period or category spend reaches 80% or breaches the limit.",
                            color = Slate300,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Primary Timeframe Limits
        item {
            Text(
                text = "Periodic Ceilings",
                color = Slate50,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Daily Limit
        item {
            val dailyLimitObj = periodLimits.find { it.periodType == "DAILY" }
            BudgetLimitProgressCard(
                title = "Daily Budget Limit",
                spent = dailySpend,
                limit = dailyLimitObj?.limitAmount ?: 0.0,
                currencySymbol = currencySymbol,
                warningThresholdPercent = dailyLimitObj?.notifyThresholdPercent ?: 80,
                onEditClick = {
                    if (dailyLimitObj != null) onEditLimitClick(dailyLimitObj)
                    else onAddLimitClick()
                }
            )
        }

        // Weekly Limit
        item {
            val weeklyLimitObj = periodLimits.find { it.periodType == "WEEKLY" }
            BudgetLimitProgressCard(
                title = "Weekly Budget Limit",
                spent = weeklySpend,
                limit = weeklyLimitObj?.limitAmount ?: 0.0,
                currencySymbol = currencySymbol,
                warningThresholdPercent = weeklyLimitObj?.notifyThresholdPercent ?: 80,
                onEditClick = {
                    if (weeklyLimitObj != null) onEditLimitClick(weeklyLimitObj)
                    else onAddLimitClick()
                }
            )
        }

        // Monthly Limit
        item {
            val monthlyLimitObj = periodLimits.find { it.periodType == "MONTHLY" }
            BudgetLimitProgressCard(
                title = "Monthly Budget Limit",
                spent = monthlySpend,
                limit = monthlyLimitObj?.limitAmount ?: 0.0,
                currencySymbol = currencySymbol,
                warningThresholdPercent = monthlyLimitObj?.notifyThresholdPercent ?: 80,
                onEditClick = {
                    if (monthlyLimitObj != null) onEditLimitClick(monthlyLimitObj)
                    else onAddLimitClick()
                }
            )
        }

        // Yearly Limit
        item {
            val yearlyLimitObj = periodLimits.find { it.periodType == "YEARLY" }
            BudgetLimitProgressCard(
                title = "Yearly Budget Limit",
                spent = yearlySpend,
                limit = yearlyLimitObj?.limitAmount ?: 0.0,
                currencySymbol = currencySymbol,
                warningThresholdPercent = yearlyLimitObj?.notifyThresholdPercent ?: 80,
                onEditClick = {
                    if (yearlyLimitObj != null) onEditLimitClick(yearlyLimitObj)
                    else onAddLimitClick()
                }
            )
        }

        // Category-Specific Budgets
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category Ceilings",
                    color = Slate50,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${categoryLimits.size} limits",
                    color = Slate400,
                    fontSize = 12.sp
                )
            }
        }

        if (categoryLimits.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No category-specific limits", color = Slate200, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Set custom caps for Dining, Shopping, Entertainment, etc.", color = Slate400, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(categoryLimits, key = { it.id }) { limit ->
                val catName = limit.categoryName ?: "Other"
                val catMeta = CategoryRegistry.getCategory(catName)
                val catSpend = expenseList
                    .filter { it.dateMillis >= startOfMonth && it.category.equals(catName, ignoreCase = true) }
                    .sumOf { it.amount }

                BudgetLimitProgressCard(
                    title = "$catName (Monthly)",
                    spent = catSpend,
                    limit = limit.limitAmount,
                    currencySymbol = currencySymbol,
                    warningThresholdPercent = limit.notifyThresholdPercent,
                    onEditClick = { onEditLimitClick(limit) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // ==========================================
    // MODAL: BUDGET ALERT POLICY & THRESHOLD RULES
    // ==========================================
    if (showInfoModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showInfoModal = false },
            sheetState = sheetState,
            containerColor = glassModalContainerColor(),
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isAppInDarkTheme()) Slate700 else Color(0xFFCBD5E1))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentIndigo.copy(alpha = 0.2f))
                            .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ceiling & Alert Rules",
                            color = Slate50,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "How automated budget breach notifications operate",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Rules Breakdown
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Rule 1: Warning threshold
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFBBF24).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Early Warning Threshold (80%)",
                                    color = Slate50,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Triggers an amber visual indicator and high-priority push warning when spending crosses your configured margin.",
                                    color = Slate400,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Rule 2: Limit Exceeded
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentRose.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AccentRose,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Hard Ceiling Breach (100%+)",
                                    color = Slate50,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Emits urgent system alert notifications, highlights the card in glowing crimson, and locks subsequent forecasts.",
                                    color = Slate400,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassButton(
                    text = "Got It",
                    onClick = { showInfoModal = false },
                    isSecondary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showAutoPlannerModal) {
        AutoBudgetPlannerModal(
            totalFundBalance = totalFundBalance,
            availableCategories = CategoryRegistry.defaultCategories.filter { !it.isIncome },
            currencySymbol = currencySymbol,
            onApplyAllocations = onApplyAutoBudget,
            onDismiss = { showAutoPlannerModal = false }
        )
    }
}
}
