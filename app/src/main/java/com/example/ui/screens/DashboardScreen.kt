package com.example.ui.screens

import java.util.Calendar
import androidx.compose.ui.graphics.StrokeCap

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.MonthlyTrendVsLimitItem
import com.example.ui.components.RechartsSpendingTrendChart
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.PeriodSummary
import com.example.ui.viewmodel.TimeframeFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import com.example.data.local.entity.FundEntity
import com.example.data.model.FinancialAdviceRepository
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import com.example.ui.modals.BudgetForecastModal
import com.example.ui.modals.CategoryBreakdownModal
import com.example.ui.modals.DailyWisdomModal
import com.example.ui.modals.SmartInsightsModal
import com.example.ui.modals.SubscriptionsModal
import com.example.ui.viewmodel.BudgetForecast
import com.example.ui.viewmodel.ForecastHealth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    transactions: List<TransactionEntity>,
    limits: List<BudgetLimitEntity>,
    funds: List<FundEntity> = emptyList(),
    selectedTimeframe: TimeframeFilter,
    periodSummary: PeriodSummary,
    budgetForecast: BudgetForecast = BudgetForecast(),
    currencySymbol: String,
    onTimeframeChange: (TimeframeFilter) -> Unit,
    onAddTransactionClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    onNavigateToLimits: () -> Unit,
    onExportCsvClick: () -> Unit = {},
    onSubscriptionsClick: () -> Unit = {},
    onGoalsClick: () -> Unit = {},
    onPostPaymentNow: (TransactionEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    var showTimeframeModal by remember { mutableStateOf(false) }
    var showPeriodInfoModal by remember { mutableStateOf(false) }
    var showDailyWisdomModal by remember { mutableStateOf(false) }
    var showCategoryModal by remember { mutableStateOf(false) }
    var showSubscriptionsModal by remember { mutableStateOf(false) }
    var showSmartInsightsModal by remember { mutableStateOf(false) }
    var showForecastModal by remember { mutableStateOf(false) }

    val todayAdvice = remember { FinancialAdviceRepository.getTodayAdvice() }
    val greeting = remember { FinancialAdviceRepository.getTimeBasedGreeting() }

    val timeframeDetails = listOf(
        Triple(
            TimeframeFilter.DAILY,
            "Daily Focus",
            "Track intra-day expenditure vs 24-hour limits"
        ),
        Triple(
            TimeframeFilter.WEEKLY,
            "Weekly Cycle",
            "Monitor ongoing 7-day spending rhythms"
        ),
        Triple(
            TimeframeFilter.MONTHLY,
            "Monthly Budget",
            "Standard monthly accounting & category caps"
        ),
        Triple(
            TimeframeFilter.YEARLY,
            "Annual Overview",
            "Long-range annual cashflow & total accruals"
        )
    )

    val isDark = isAppInDarkTheme()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Clean, Minimal Header with Brand Logo & Greeting
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate900.copy(alpha = 0.9f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pennywise_logo),
                                contentDescription = "PennyWise Logo",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PennyWise",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "$greeting 👋",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Live Local Status Pill
                    val isDark = isAppInDarkTheme()
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Slate900.copy(alpha = 0.8f) else Slate100)
                            .border(1.dp, if (isDark) Slate800 else Slate300, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (periodSummary.isLimitExceeded) AccentRose else AccentEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (periodSummary.isLimitExceeded) "Exceeded" else "Live Local",
                                color = if (periodSummary.isLimitExceeded) AccentRose else AccentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Daily Financial Wisdom Banner Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isDark) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Slate900.copy(alpha = 0.95f),
                                        Slate950.copy(alpha = 0.95f)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFF1F5F9),
                                        Color(0xFFE2E8F0)
                                    )
                                )
                            }
                        )
                        .border(1.dp, if (isDark) AccentCyan.copy(alpha = 0.25f) else AccentCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { showDailyWisdomModal = true }
                        .testTag("welcome_daily_wisdom_banner")
                ) {
                    // Ambient Graphic Canvas Glow
                    Canvas(
                        modifier = Modifier.matchParentSize()
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(AccentCyan.copy(alpha = 0.18f), Color.Transparent),
                                center = androidx.compose.ui.geometry.Offset(size.width * 0.92f, size.height * 0.5f),
                                radius = size.width * 0.38f
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "“${todayAdvice.quote}”",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "— ${todayAdvice.author}  •  Daily Wisdom 💡",
                                color = if (isDark) AccentCyan else Color(0xFF0284C7),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Main Content Toolbar: Timeframe Selector & Period Diagnostics Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timeframe Modal Trigger Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Slate900.copy(alpha = 0.9f) else Color.White)
                            .border(1.dp, if (isDark) Slate800 else Slate300, RoundedCornerShape(14.dp))
                            .clickable { showTimeframeModal = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("timeframe_selector_pill"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = selectedTimeframe.displayName,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Open Timeframe Modal",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Summary Info Modal Trigger Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Slate900.copy(alpha = 0.9f) else Color.White)
                            .border(1.dp, if (isDark) Slate800 else Slate300, RoundedCornerShape(14.dp))
                            .clickable { showPeriodInfoModal = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("dashboard_info_modal_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QueryStats,
                                contentDescription = "View Period Diagnostics",
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Diagnostics",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 1. FINANCIAL ASSESSMENT SUMMARY INFOGRAPHIC
            // ==========================================
            item {
                val cal = Calendar.getInstance()
                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                val monthProgress = dayOfMonth.toFloat() / maxDays.toFloat()

                val healthScore = when {
                    periodSummary.isLimitExceeded -> 45
                    periodSummary.limitAmount > 0 && periodSummary.limitRatio > monthProgress -> 72
                    else -> 92
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("infographic_assessment_card"),
                    shape = RoundedCornerShape(24.dp),
                    onClick = { showSmartInsightsModal = true }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentIndigo.copy(alpha = 0.25f), Color.Transparent),
                                    center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.2f),
                                    radius = size.width * 0.45f
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AccentIndigo.copy(alpha = 0.2f))
                                            .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = AccentIndigo,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "FINANCIAL ASSESSMENT",
                                            color = Slate400,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "Health & Pacing Summary",
                                            color = Slate50,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                ShadcnBadge(
                                    text = if (healthScore >= 80) "Optimal" else if (healthScore >= 60) "Caution" else "Alert",
                                    variant = if (healthScore >= 80) BadgeVariant.CYAN else BadgeVariant.DESTRUCTIVE
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier.size(90.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.matchParentSize()) {
                                        val strokeWidth = 8.dp.toPx()
                                        drawArc(
                                            color = Slate800,
                                            startAngle = 135f,
                                            sweepAngle = 270f,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                        val sweep = 270f * (healthScore / 100f)
                                        drawArc(
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    if (healthScore >= 80) AccentCyan else AccentRose,
                                                    if (healthScore >= 80) AccentIndigo else Color(0xFFF59E0B)
                                                )
                                            ),
                                            startAngle = 135f,
                                            sweepAngle = sweep,
                                            useCenter = false,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$healthScore",
                                            color = Slate50,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "SCORE",
                                            color = Slate400,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Net Cashflow", color = Slate400, fontSize = 12.sp)
                                        Text(
                                            text = "${if (periodSummary.netBalance >= 0) "+" else ""}$currencySymbol${String.format(Locale.US, "%,.2f", periodSummary.netBalance)}",
                                            color = if (periodSummary.netBalance >= 0) AccentEmerald else AccentRose,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (periodSummary.limitAmount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Slate800)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(periodSummary.limitRatio.coerceIn(0f, 1f))
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (periodSummary.isLimitExceeded) AccentRose else AccentCyan
                                                    )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${(periodSummary.limitRatio * 100).toInt()}% Used",
                                                color = Slate400,
                                                fontSize = 10.sp
                                            )
                                            Text(
                                                text = "Cap: $currencySymbol${String.format(Locale.US, "%,.0f", periodSummary.limitAmount)}",
                                                color = Slate300,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    } else {
                                        Text("No active budget cap set", color = Slate400, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // BUDGET FORECAST WIDGET
            // ==========================================
            item {
                val forecastHealthText = when (budgetForecast.healthStatus) {
                    ForecastHealth.ON_TRACK -> "On Track"
                    ForecastHealth.CAUTION -> "Caution Needed"
                    ForecastHealth.OVER_BUDGET -> "Over Budget Projected"
                }

                val forecastBadgeVariant = when (budgetForecast.healthStatus) {
                    ForecastHealth.ON_TRACK -> BadgeVariant.SUCCESS
                    ForecastHealth.CAUTION -> BadgeVariant.WARNING
                    ForecastHealth.OVER_BUDGET -> BadgeVariant.DESTRUCTIVE
                }

                val forecastColor = when (budgetForecast.healthStatus) {
                    ForecastHealth.ON_TRACK -> AccentEmerald
                    ForecastHealth.CAUTION -> Color(0xFFF59E0B)
                    ForecastHealth.OVER_BUDGET -> AccentRose
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("budget_forecast_widget"),
                    onClick = { showForecastModal = true }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShowChart,
                                        contentDescription = "Budget Forecast",
                                        tint = AccentViolet,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = "BUDGET FORECAST",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "End-of-Month Projection",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            ShadcnBadge(
                                text = forecastHealthText,
                                variant = forecastBadgeVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = "Projected EOM Remaining",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", budgetForecast.projectedRemainingBalance)}",
                                    color = forecastColor,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Daily Burn Rate",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%.2f", budgetForecast.averageDailySpend)}/day",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) Slate900.copy(alpha = 0.6f) else Slate100)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚡ Target Pace: $currencySymbol${String.format(Locale.US, "%.2f", budgetForecast.recommendedDailyAllowance)}/day remaining",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f, fill = false),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Analysis →",
                                    color = AccentViolet,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // ==========================================
            // RECHARTS MONTHLY SPENDING TRENDS VS LIMITS
            // ==========================================
            item {
                val monthlyTrends = remember(transactions, limits) {
                    val expenses = transactions.filter { it.type == "EXPENSE" }
                    val monthlyLimitObj = limits.find { it.periodType == "MONTHLY" && it.isEnabled }
                    val explicitLimit = monthlyLimitObj?.limitAmount ?: 0.0
                    val categoryMonthlyTotal = limits.filter { it.periodType == "CATEGORY" && it.isEnabled }.sumOf { it.limitAmount }
                    val activeLimit = if (explicitLimit > 0) explicitLimit else if (categoryMonthlyTotal > 0) categoryMonthlyTotal else 1500.0

                    val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                    val fullFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

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
                                monthLabel = monthFormat.format(Date(startMs)),
                                fullMonthName = fullFormat.format(Date(startMs)),
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
                    modifier = Modifier.testTag("dashboard_recharts_trend_chart")
                )
            }

            // ==========================================
            // 2. EXPENSES INFOGRAPHIC
            // ==========================================
            item {
                val expenses = transactions.filter { it.type == "EXPENSE" }
                val totalExpense = expenses.sumOf { it.amount }
                val categoryGroups = expenses.groupBy { it.category }
                    .map { (catId, list) ->
                        val sum = list.sumOf { it.amount }
                        val pct = if (totalExpense > 0) (sum / totalExpense).toFloat() else 0f
                        Triple(CategoryRegistry.getCategory(catId), sum, pct)
                    }
                    .sortedByDescending { it.second }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("infographic_expenses_card"),
                    shape = RoundedCornerShape(24.dp),
                    onClick = { showCategoryModal = true }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentCyan.copy(alpha = 0.18f), Color.Transparent),
                                    center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.8f),
                                    radius = size.width * 0.4f
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AccentCyan.copy(alpha = 0.2f))
                                            .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PieChart,
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "EXPENSES INFOGRAPHIC",
                                            color = Slate400,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalExpense)}",
                                            color = Slate50,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "Details →",
                                    color = AccentCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (categoryGroups.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Slate800)
                                ) {
                                    categoryGroups.forEach { (catMeta, _, pct) ->
                                        if (pct > 0.01f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(pct)
                                                    .height(10.dp)
                                                    .background(catMeta.color)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    categoryGroups.take(3).forEach { (catMeta, sum, pct) ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Slate900.copy(alpha = 0.6f))
                                                .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                                .padding(8.dp)
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(catMeta.color)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = catMeta.name,
                                                        color = Slate300,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        maxLines = 1
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${(pct * 100).toInt()}%",
                                                    color = Slate50,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("No expenses recorded yet.", color = Slate400, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // ==========================================
            // 3. FUNDS INFOGRAPHIC
            // ==========================================
            item {
                val totalSaved = funds.sumOf { it.balance }
                val totalTarget = funds.sumOf { it.targetAmount ?: 0.0 }
                val overallProgress = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 1f

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("infographic_funds_card"),
                    shape = RoundedCornerShape(24.dp),
                    onClick = onGoalsClick
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentEmerald.copy(alpha = 0.2f), Color.Transparent),
                                    center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.8f),
                                    radius = size.width * 0.45f
                                )
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(AccentEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Savings,
                                            contentDescription = null,
                                            tint = AccentEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "FUNDS & SAVINGS TARGETS",
                                            color = Slate400,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalSaved)}",
                                            color = Slate50,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = "All Funds →",
                                    color = AccentEmerald,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (totalTarget > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Slate800)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(overallProgress)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                Brush.horizontalGradient(listOf(AccentEmerald, AccentCyan))
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${(overallProgress * 100).toInt()}% Saved of Goal",
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "Target: $currencySymbol${String.format(Locale.US, "%,.0f", totalTarget)}",
                                        color = AccentEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Text("Tap to configure savings targets and funds.", color = Slate400, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra padding for FAB & BottomNav
            }
        }

        // Glowing Floating Action Button
        FloatingActionButton(
            onClick = onAddTransactionClick,
            containerColor = AccentCyan,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .testTag("add_transaction_fab")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Transaction",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    // ==========================================
    // MODAL: TIMEFRAME SELECTION SHEET
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
                            text = "Filter Time Horizon",
                            color = Slate50,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Select time horizon for summaries and charts",
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

    // ==========================================
    // MODAL: PERIOD DIAGNOSTICS & DETAILS SHEET
    // ==========================================
    if (showPeriodInfoModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showPeriodInfoModal = false },
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
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${selectedTimeframe.displayName} Period Metrics",
                            color = Slate50,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Live status of limits, cashflow & headroom",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Breakdown Stats
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Expenses", color = Slate400, fontSize = 13.sp)
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", periodSummary.totalExpenses)}",
                                color = Slate100,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total Inflow", color = Slate400, fontSize = 13.sp)
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", periodSummary.totalIncome)}",
                                color = AccentEmerald,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Active Budget Cap", color = Slate400, fontSize = 13.sp)
                            Text(
                                text = if (periodSummary.limitAmount > 0) "$currencySymbol${String.format(Locale.US, "%,.2f", periodSummary.limitAmount)}" else "No Ceiling Set",
                                color = Slate200,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Remaining Headroom", color = Slate400, fontSize = 13.sp)
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", periodSummary.remainingAmount.coerceAtLeast(0.0))}",
                                color = if (periodSummary.isLimitExceeded) AccentRose else AccentCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassButton(
                    text = "Close Diagnostics",
                    onClick = { showPeriodInfoModal = false },
                    isSecondary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // ==========================================
    // MODAL: DAILY FINANCIAL WISDOM MODAL
    // ==========================================
    if (showDailyWisdomModal) {
        DailyWisdomModal(
            onDismiss = { showDailyWisdomModal = false }
        )
    }

    if (showCategoryModal) {
        CategoryBreakdownModal(
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showCategoryModal = false }
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

    if (showSubscriptionsModal) {
        SubscriptionsModal(
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showSubscriptionsModal = false },
            onAddRecurringClick = {
                showSubscriptionsModal = false
                onAddTransactionClick()
            },
            onPostPaymentNow = onPostPaymentNow
        )
    }

    if (showForecastModal) {
        BudgetForecastModal(
            forecast = budgetForecast,
            currencySymbol = currencySymbol,
            onDismiss = { showForecastModal = false }
        )
    }
}
