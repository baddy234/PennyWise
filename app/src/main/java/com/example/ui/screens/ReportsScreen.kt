package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import com.example.ui.components.BadgeVariant
import com.example.ui.components.BarChartItem
import com.example.ui.components.BarSpendingChart
import com.example.ui.components.CategoryProgressRow
import com.example.ui.components.CategorySpendItem
import com.example.ui.components.DonutSpendChart
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.MonthlyTrendVsLimitItem
import com.example.ui.components.RechartsSpendingTrendChart
import com.example.ui.components.ShadcnBadge
import com.example.ui.modals.CategoryBreakdownModal
import com.example.ui.modals.SmartInsightsModal
import com.example.ui.theme.*
import com.example.ui.viewmodel.InsightType
import com.example.ui.viewmodel.PeriodSummary
import com.example.ui.viewmodel.SpendingInsight
import com.example.ui.viewmodel.TimeframeFilter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    transactions: List<TransactionEntity>,
    limits: List<BudgetLimitEntity>,
    selectedTimeframe: TimeframeFilter,
    periodSummary: PeriodSummary,
    categoryBreakdown: List<CategorySpendItem>,
    barChartData: List<BarChartItem>,
    insights: List<SpendingInsight>,
    currencySymbol: String,
    onTimeframeChange: (TimeframeFilter) -> Unit,
    onExportCsvClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showTimeframeModal by remember { mutableStateOf(false) }
    var showCategoryBreakdownModal by remember { mutableStateOf(false) }
    var showSmartInsightsModal by remember { mutableStateOf(false) }

    val timeframeDetails = listOf(
        Triple(TimeframeFilter.DAILY, "Daily Analytics", "Analyze today's timestamped expenses"),
        Triple(TimeframeFilter.WEEKLY, "Weekly Trend", "Examine 7-day cyclical distribution"),
        Triple(TimeframeFilter.MONTHLY, "Monthly Breakdown", "Deep dive into this month's category splits"),
        Triple(TimeframeFilter.YEARLY, "Annual Summary", "Full year macro financial aggregations")
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
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
                            text = "Visual Reports",
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
                                    .background(AccentCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${categoryBreakdown.size} Categories Active",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    ShadcnBadge(
                        text = selectedTimeframe.displayName,
                        variant = BadgeVariant.CYAN
                    )
                }
            }

            // Main Content Toolbar: Timeframe Modal Trigger & Export CSV Button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timeframe Modal Trigger Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900.copy(alpha = 0.85f))
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .clickable { showTimeframeModal = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("reports_timeframe_selector_pill"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "${selectedTimeframe.displayName} Period",
                                color = Slate100,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Open Timeframe Modal",
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Export CSV Action Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentEmerald.copy(alpha = 0.15f))
                            .border(1.dp, AccentEmerald.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .clickable { onExportCsvClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("reports_export_csv_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Export CSV",
                            tint = AccentEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Export CSV",
                            color = AccentEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        // Donut Chart Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Category Distribution",
                                color = Slate50,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        ShadcnBadge(
                            text = "${categoryBreakdown.size} Categories",
                            variant = BadgeVariant.SECONDARY
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    DonutSpendChart(
                        items = categoryBreakdown,
                        totalAmount = periodSummary.totalExpense,
                        currencySymbol = currencySymbol,
                        onCategoryClick = { showCategoryBreakdownModal = true }
                    )
                }
            }
        }

        // Spending Trend Bar Chart Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Spending Timeline",
                                color = textPrimaryColor(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = when (selectedTimeframe) {
                                TimeframeFilter.DAILY -> "Last 7 Days"
                                TimeframeFilter.WEEKLY -> "Last 4 Weeks"
                                TimeframeFilter.MONTHLY -> "Last 6 Months"
                                TimeframeFilter.YEARLY -> "Last 4 Years"
                            },
                            color = textMutedColor(),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BarSpendingChart(
                        bars = barChartData,
                        currencySymbol = currencySymbol
                    )
                }
            }
        }

        // Recharts Dual Axis Trend vs Limit Chart
        item {
            val monthlyTrends = remember(transactions, limits) {
                val expenses = transactions.filter { it.type == "EXPENSE" }
                val monthlyLimitObj = limits.find { it.periodType == "MONTHLY" && it.isEnabled }
                val explicitLimit = monthlyLimitObj?.limitAmount ?: 0.0
                val categoryMonthlyTotal = limits.filter { it.periodType == "CATEGORY" && it.isEnabled }.sumOf { it.limitAmount }
                val activeLimit = if (explicitLimit > 0) explicitLimit else if (categoryMonthlyTotal > 0) categoryMonthlyTotal else 1500.0

                val monthFormat = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
                val fullFormat = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())

                val result = mutableListOf<MonthlyTrendVsLimitItem>()
                val nowCal = java.util.Calendar.getInstance()
                val currentMonthIdx = nowCal.get(java.util.Calendar.MONTH)
                val currentYearIdx = nowCal.get(java.util.Calendar.YEAR)

                for (m in 5 downTo 0) {
                    val c = java.util.Calendar.getInstance()
                    c.add(java.util.Calendar.MONTH, -m)
                    c.set(java.util.Calendar.DAY_OF_MONTH, 1)
                    c.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    c.set(java.util.Calendar.MINUTE, 0)
                    c.set(java.util.Calendar.SECOND, 0)
                    c.set(java.util.Calendar.MILLISECOND, 0)
                    val startMs = c.timeInMillis

                    c.add(java.util.Calendar.MONTH, 1)
                    val endMs = c.timeInMillis

                    val monthSpend = expenses.filter { it.dateMillis in startMs until endMs }.sumOf { it.amount }

                    c.timeInMillis = startMs
                    val isCurrent = (c.get(java.util.Calendar.MONTH) == currentMonthIdx && c.get(java.util.Calendar.YEAR) == currentYearIdx)

                    result.add(
                        MonthlyTrendVsLimitItem(
                            monthLabel = monthFormat.format(java.util.Date(startMs)),
                            fullMonthName = fullFormat.format(java.util.Date(startMs)),
                            actualSpending = monthSpend,
                            setLimit = activeLimit,
                            isCurrentMonth = isCurrent
                        )
                    )
                }
                result
            }

            RechartsSpendingTrendChart(
                items = monthlyTrends,
                currencySymbol = currencySymbol,
                modifier = Modifier.testTag("reports_recharts_trend_chart")
            )
        }

        // Spending Insights Cards Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Financial Insights",
                        color = textPrimaryColor(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentCyan.copy(alpha = 0.15f))
                        .border(1.dp, AccentCyan.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { showSmartInsightsModal = true }
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Deep-Dive AI",
                        color = AccentCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(insights) { insight ->
            val (icon, tint, border) = when (insight.type) {
                InsightType.POSITIVE -> Triple(Icons.Default.CheckCircle, AccentEmerald, AccentEmerald)
                InsightType.WARNING -> Triple(Icons.Default.Warning, Color(0xFFFBBF24), Color(0xFFFBBF24))
                InsightType.DANGER -> Triple(Icons.Default.Warning, AccentRose, AccentRose)
                InsightType.INFO -> Triple(Icons.Default.Info, AccentCyan, AccentCyan)
            }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlowColor = border.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                onClick = { showSmartInsightsModal = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(tint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = insight.title,
                            color = textPrimaryColor(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = insight.description,
                            color = textSecondaryColor(),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Categorized Breakdown Detail List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categorized Breakdown",
                    color = textPrimaryColor(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (categoryBreakdown.isNotEmpty()) {
                    Text(
                        text = "Tap for transactions",
                        color = textMutedColor(),
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { showCategoryBreakdownModal = true }
                    )
                }
            }
        }

        if (categoryBreakdown.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No spending in this timeframe", color = textMutedColor(), fontSize = 13.sp)
                    }
                }
            }
        } else {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { showCategoryBreakdownModal = true }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        categoryBreakdown.forEachIndexed { index, item ->
                            CategoryProgressRow(
                                item = item,
                                currencySymbol = currencySymbol
                            )
                            if (index < categoryBreakdown.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // ==========================================
    // MODALS: CATEGORY BREAKDOWN & AI INSIGHTS
    // ==========================================
    if (showCategoryBreakdownModal) {
        CategoryBreakdownModal(
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showCategoryBreakdownModal = false }
        )
    }

    if (showSmartInsightsModal) {
        SmartInsightsModal(
            periodSummary = periodSummary,
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showSmartInsightsModal = false }
        )
    }
}

    // ==========================================
    // MODAL: REPORTS TIMEFRAME SELECTOR
    // ==========================================
    if (showTimeframeModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showTimeframeModal = false },
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Analytics Scope",
                            color = Slate50,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Choose timeframe for charts & category splits",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }

                    ShadcnBadge(
                        text = "Active: ${selectedTimeframe.displayName}",
                        variant = BadgeVariant.CYAN
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    timeframeDetails.forEach { (tf, title, desc) ->
                        val isSelected = selectedTimeframe == tf
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) AccentCyan.copy(alpha = 0.16f) else Slate900)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentCyan.copy(alpha = 0.8f) else Slate800,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onTimeframeChange(tf)
                                    showTimeframeModal = false
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) AccentCyan else Slate800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.Black else Slate300,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = title,
                                        color = if (isSelected) Slate50 else Slate200,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = desc,
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AccentCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
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
