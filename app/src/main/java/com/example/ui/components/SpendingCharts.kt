package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.*
import java.util.Locale

data class CategorySpendItem(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

data class BarChartItem(
    val label: String,
    val amount: Double,
    val isHighlighted: Boolean = false
)

/**
 * Animated Donut Chart with center total, interactive slice/legend selection, and category drilldown.
 */
@Composable
fun DonutSpendChart(
    items: List<CategorySpendItem>,
    totalAmount: Double,
    currencySymbol: String = "$",
    onCategoryClick: ((CategorySpendItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember(items) { mutableStateOf(-1) }

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 900),
        label = "donut_anim"
    )

    val isDark = isAppInDarkTheme()
    val selectedItem = items.getOrNull(selectedIndex)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                val strokeWidth = 24.dp.toPx()
                val selectedStrokeWidth = 32.dp.toPx()
                val arcSize = size.width - selectedStrokeWidth
                val topLeft = Offset(selectedStrokeWidth / 2f, selectedStrokeWidth / 2f)

                if (items.isEmpty() || totalAmount <= 0) {
                    drawArc(
                        color = if (isDark) Color(0x22FFFFFF) else Color(0x22000000),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = strokeWidth)
                    )
                } else {
                    var startAngle = -90f
                    for ((idx, item) in items.withIndex()) {
                        val sweepAngle = (item.percentage / 100f) * 360f * animProgress
                        val isSelected = (idx == selectedIndex)
                        val currentStroke = if (isSelected) selectedStrokeWidth else strokeWidth

                        if (sweepAngle > 0f) {
                            drawArc(
                                color = if (isSelected) item.color else item.color.copy(alpha = 0.85f),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle.coerceAtLeast(2f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = Size(arcSize, arcSize),
                                style = Stroke(width = currentStroke, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                }
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        if (selectedItem != null) {
                            onCategoryClick?.invoke(selectedItem)
                        } else {
                            selectedIndex = if (items.isNotEmpty()) 0 else -1
                        }
                    }
                    .padding(8.dp)
            ) {
                Text(
                    text = selectedItem?.category ?: "Total Spent",
                    color = textMutedColor(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", selectedItem?.amount ?: totalAmount)}",
                    color = textPrimaryColor(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                if (selectedItem != null) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", selectedItem.percentage)}% of total",
                        color = AccentCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Category Legend Pills
        if (items.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                items.take(4).forEachIndexed { idx, item ->
                    val isSelected = (idx == selectedIndex)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) item.color.copy(alpha = 0.2f) else glassSurfaceBgColor())
                            .border(
                                1.dp,
                                if (isSelected) item.color else glassBorderColor(),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedIndex = if (isSelected) -1 else idx
                                onCategoryClick?.invoke(item)
                            }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(item.color)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = item.category,
                            color = if (isSelected) textPrimaryColor() else textSecondaryColor(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clean Interactive Spending Bar Chart with tapable bar tooltips.
 */
@Composable
fun BarSpendingChart(
    bars: List<BarChartItem>,
    currencySymbol: String = "$",
    onBarClick: ((BarChartItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (bars.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No spending recorded for this period", color = textMutedColor(), fontSize = 13.sp)
        }
        return
    }

    var selectedIndex by remember(bars) {
        mutableStateOf(bars.indexOfFirst { it.isHighlighted }.takeIf { it >= 0 } ?: (bars.size - 1))
    }

    val maxAmount = (bars.maxOfOrNull { it.amount } ?: 100.0).coerceAtLeast(50.0)
    val selectedBar = bars.getOrNull(selectedIndex)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Selected Bar Interactive Callout Banner
        if (selectedBar != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (selectedBar.isHighlighted) AccentCyan else AccentViolet)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Period: ${selectedBar.label}",
                        color = textMutedColor(),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", selectedBar.amount)}",
                    color = textPrimaryColor(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEachIndexed { idx, bar ->
                val isSelected = (idx == selectedIndex)
                val ratio = (bar.amount / maxAmount).toFloat().coerceIn(0.04f, 1f)
                val animatedRatio by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = tween(durationMillis = 650),
                    label = "bar_anim"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedIndex = idx
                            onBarClick?.invoke(bar)
                        }
                ) {
                    if (bar.amount > 0) {
                        Text(
                            text = "$currencySymbol${bar.amount.toInt()}",
                            color = if (isSelected || bar.isHighlighted) AccentCyan else textMutedColor(),
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Bar Column
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height((90 * animatedRatio).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                            .background(
                                if (isSelected) {
                                    Brush.verticalGradient(listOf(AccentCyan, AccentIndigo))
                                } else if (bar.isHighlighted) {
                                    Brush.verticalGradient(listOf(AccentCyan.copy(alpha = 0.8f), AccentIndigo.copy(alpha = 0.5f)))
                                } else if (bar.amount > 0) {
                                    Brush.verticalGradient(listOf(AccentViolet.copy(alpha = 0.7f), AccentIndigo.copy(alpha = 0.3f)))
                                } else {
                                    Brush.verticalGradient(listOf(Color(0x18FFFFFF), Color(0x08FFFFFF)))
                                }
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) AccentCyan else if (bar.isHighlighted) AccentCyan.copy(alpha = 0.5f) else Color(0x15FFFFFF),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = bar.label,
                        color = if (isSelected || bar.isHighlighted) textPrimaryColor() else textMutedColor(),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected || bar.isHighlighted) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

/**
 * Budget limit card with linear progress, alert badge, and remaining funds.
 */
@Composable
fun BudgetLimitProgressCard(
    title: String,
    spent: Double,
    limit: Double,
    currencySymbol: String = "$",
    warningThresholdPercent: Int = 80,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ratio = if (limit > 0) (spent / limit).toFloat() else 0f
    val isExceeded = spent > limit && limit > 0
    val isWarning = !isExceeded && (ratio * 100) >= warningThresholdPercent && limit > 0
    val remaining = (limit - spent).coerceAtLeast(0.0)
    val excess = (spent - limit).coerceAtLeast(0.0)

    val progressAnim by animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "progress_limit"
    )

    val glowBorder = when {
        isExceeded -> AccentRose
        isWarning -> Color(0xFFFBBF24)
        else -> null
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderGlowColor = glowBorder,
        onClick = onEditClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = Slate50,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isExceeded) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Exceeded Limit",
                            tint = AccentRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                when {
                    isExceeded -> ShadcnBadge(text = "+$currencySymbol${String.format(java.util.Locale.US, "%.0f", excess)} Over", variant = BadgeVariant.DESTRUCTIVE)
                    isWarning -> ShadcnBadge(text = "${(ratio * 100).toInt()}% Warning", variant = BadgeVariant.WARNING)
                    limit <= 0 -> ShadcnBadge(text = "No Limit Set", variant = BadgeVariant.OUTLINE)
                    else -> ShadcnBadge(text = "${(ratio * 100).toInt()}% Used", variant = BadgeVariant.SUCCESS)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linear Progress Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x22FFFFFF))
            ) {
                val fillBrush = when {
                    isExceeded -> Brush.horizontalGradient(listOf(Color(0xFFF59E0B), AccentRose))
                    isWarning -> Brush.horizontalGradient(listOf(AccentCyan, Color(0xFFFBBF24)))
                    else -> Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressAnim)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(fillBrush)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Spent",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$currencySymbol${String.format(java.util.Locale.US, "%,.2f", spent)}",
                        color = if (isExceeded) AccentRose else Slate200,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isExceeded) "Over Limit" else "Remaining",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (isExceeded) {
                            "-$currencySymbol${String.format(java.util.Locale.US, "%,.2f", excess)}"
                        } else {
                            "$currencySymbol${String.format(java.util.Locale.US, "%,.2f", remaining)}"
                        },
                        color = if (isExceeded) AccentRose else AccentEmerald,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Category breakdown list row with icon, progress bar, percentage, and amount.
 */
@Composable
fun CategoryProgressRow(
    item: CategorySpendItem,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val categoryMeta = CategoryRegistry.getCategory(item.category)
    val progress by animateFloatAsState(
        targetValue = (item.percentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "cat_prog"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(categoryMeta.color.copy(alpha = 0.15f))
                .border(1.dp, categoryMeta.color.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryMeta.icon,
                contentDescription = item.category,
                tint = categoryMeta.color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.category,
                    color = Slate50,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$currencySymbol${String.format(java.util.Locale.US, "%,.2f", item.amount)}",
                    color = Slate200,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0x18FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(item.color)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${String.format(java.util.Locale.US, "%.1f", item.percentage)}%",
                    color = textMutedColor(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class MonthlyTrendVsLimitItem(
    val monthLabel: String,
    val fullMonthName: String,
    val actualSpending: Double,
    val setLimit: Double,
    val isCurrentMonth: Boolean = false
) {
    val variance: Double get() = setLimit - actualSpending
    val isExceeded: Boolean get() = setLimit > 0 && actualSpending > setLimit
    val percentUsed: Float get() = if (setLimit > 0) ((actualSpending / setLimit) * 100).toFloat() else 0f
}

/**
 * Recharts-inspired Dashboard Component for Monthly Spending Trends vs. Set Limits.
 * Provides interactive area/bar charts, floating tooltip, and dual legend.
 */
@Composable
fun RechartsSpendingTrendChart(
    items: List<MonthlyTrendVsLimitItem>,
    currencySymbol: String = "$",
    onMonthSelect: ((MonthlyTrendVsLimitItem) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    var selectedIndex by remember(items) {
        mutableStateOf(items.indexOfFirst { it.isCurrentMonth }.takeIf { it >= 0 } ?: (items.size - 1))
    }

    var viewMode by remember { mutableStateOf("AREA") } // "AREA" vs "BAR"

    val isDark = isAppInDarkTheme()
    val totalSpending = items.sumOf { it.actualSpending }
    val totalLimit = items.sumOf { it.setLimit }
    val totalVariance = totalLimit - totalSpending
    val selectedItem = items.getOrNull(selectedIndex) ?: items.last()

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Row: Recharts Badge & Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentCyan.copy(alpha = 0.2f))
                            .border(1.dp, AccentCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = "Recharts Trends",
                            tint = AccentCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RECHARTS ENGINE",
                                color = textMutedColor(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ShadcnBadge(text = "Dual Axis", variant = BadgeVariant.CYAN)
                        }
                        Text(
                            text = "Monthly Spend vs. Set Limit",
                            color = textPrimaryColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Area vs Bar chart view mode switch
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (viewMode == "AREA") AccentCyan else Color.Transparent)
                            .clickable { viewMode = "AREA" }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Curve",
                            color = if (viewMode == "AREA") Color.Black else textSecondaryColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (viewMode == "BAR") AccentCyan else Color.Transparent)
                            .clickable { viewMode = "BAR" }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Bar",
                            color = if (viewMode == "BAR") Color.Black else textSecondaryColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Summary Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) Slate900.copy(alpha = 0.6f) else Color(0xFFF1F5F9))
                    .border(1.dp, if (isDark) Slate800 else Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Period Spend", color = textMutedColor(), fontSize = 10.sp)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalSpending)}",
                        color = textPrimaryColor(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Set Cap", color = textMutedColor(), fontSize = 10.sp)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalLimit)}",
                        color = textSecondaryColor(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Period Variance", color = textMutedColor(), fontSize = 10.sp)
                    val varianceLabel = if (totalVariance >= 0) {
                        "-$currencySymbol${String.format(Locale.US, "%,.0f", totalVariance)} Under"
                    } else {
                        "+$currencySymbol${String.format(Locale.US, "%,.0f", -totalVariance)} Over"
                    }
                    Text(
                        text = varianceLabel,
                        color = if (totalVariance >= 0) AccentEmerald else AccentRose,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Canvas Chart with Touch / Tap Points
            val maxVal = maxOf(items.maxOf { it.actualSpending }, items.maxOf { it.setLimit }, 100.0) * 1.15

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, bottom = 24.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val pointCount = items.size
                    val stepX = if (pointCount > 1) w / (pointCount - 1) else w

                    // 1. Draw horizontal grid lines
                    val gridLines = 4
                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    for (i in 0..gridLines) {
                        val y = h * (1f - i.toFloat() / gridLines)
                        drawLine(
                            color = if (isDark) Color(0x22FFFFFF) else Color(0x1F000000),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f,
                            pathEffect = pathEffect
                        )
                    }

                    // 2. Draw Set Limit Reference Line
                    val limitPath = Path()
                    items.forEachIndexed { i, item ->
                        val x = i * stepX
                        val y = (h * (1f - (item.setLimit / maxVal))).toFloat()
                        if (i == 0) limitPath.moveTo(x, y) else limitPath.lineTo(x, y)
                    }
                    drawPath(
                        path = limitPath,
                        color = AccentRose.copy(alpha = 0.85f),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f),
                            cap = StrokeCap.Round
                        )
                    )

                    // 3. Render Area Curve / Bar Visualization
                    if (viewMode == "AREA") {
                        val spendPath = Path()
                        val areaPath = Path()

                        items.forEachIndexed { i, item ->
                            val x = i * stepX
                            val y = (h * (1f - (item.actualSpending / maxVal))).toFloat()
                            if (i == 0) {
                                spendPath.moveTo(x, y)
                                areaPath.moveTo(x, h)
                                areaPath.lineTo(x, y)
                            } else {
                                val prevX = (i - 1) * stepX
                                val prevY = (h * (1f - (items[i - 1].actualSpending / maxVal))).toFloat()
                                val controlX1 = prevX + (x - prevX) / 2f
                                val controlX2 = prevX + (x - prevX) / 2f
                                spendPath.cubicTo(controlX1, prevY, controlX2, y, x, y)
                                areaPath.cubicTo(controlX1, prevY, controlX2, y, x, y)
                            }
                            if (i == items.size - 1) {
                                areaPath.lineTo(x, h)
                                areaPath.close()
                            }
                        }

                        // Fill Gradient Area
                        drawPath(
                            path = areaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(AccentCyan.copy(alpha = 0.35f), AccentIndigo.copy(alpha = 0.05f))
                            )
                        )

                        // Draw Spending Line Curve
                        drawPath(
                            path = spendPath,
                            brush = Brush.horizontalGradient(listOf(AccentCyan, AccentViolet)),
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    } else {
                        // BAR MODE
                        val barWidth = (w / pointCount) * 0.45f
                        items.forEachIndexed { i, item ->
                            val cx = i * stepX
                            val barH = (h * (item.actualSpending / maxVal)).toFloat().coerceAtLeast(4f)
                            val topY = h - barH
                            val isSel = i == selectedIndex

                            drawRoundRect(
                                brush = if (isSel) Brush.verticalGradient(listOf(AccentCyan, AccentIndigo))
                                else Brush.verticalGradient(listOf(AccentViolet.copy(alpha = 0.7f), AccentIndigo.copy(alpha = 0.4f))),
                                topLeft = Offset(cx - barWidth / 2f, topY),
                                size = Size(barWidth, barH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                            )
                        }
                    }

                    // 4. Draw Data Node Circles
                    items.forEachIndexed { i, item ->
                        val x = i * stepX
                        val y = (h * (1f - (item.actualSpending / maxVal))).toFloat()
                        val isSel = i == selectedIndex

                        if (isSel) {
                            drawCircle(
                                color = AccentCyan.copy(alpha = 0.3f),
                                radius = 12.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawCircle(
                            color = if (isSel) AccentCyan else AccentIndigo,
                            radius = if (isSel) 6.dp.toPx() else 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = if (isSel) 3.dp.toPx() else 2.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                // Interactive Clickable Columns overlay
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items.forEachIndexed { idx, item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable {
                                    selectedIndex = idx
                                    onMonthSelect?.invoke(item)
                                },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = item.monthLabel,
                                color = if (idx == selectedIndex) textPrimaryColor() else textMutedColor(),
                                fontSize = 11.sp,
                                fontWeight = if (idx == selectedIndex) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts Floating Interactive Tooltip Card
            selectedItem.let { sel ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isDark) {
                                Brush.linearGradient(listOf(Slate900, Slate950))
                            } else {
                                Brush.linearGradient(listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9)))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (sel.isExceeded) AccentRose.copy(alpha = 0.6f) else AccentCyan.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (sel.isExceeded) AccentRose else AccentCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = sel.fullMonthName.uppercase(Locale.US),
                                    color = textMutedColor(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", sel.actualSpending)}",
                                    color = textPrimaryColor(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "vs $currencySymbol${String.format(Locale.US, "%,.0f", sel.setLimit)} limit",
                                    color = textMutedColor(),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            ShadcnBadge(
                                text = if (sel.isExceeded) "Exceeded Cap" else "${sel.percentUsed.toInt()}% Used",
                                variant = if (sel.isExceeded) BadgeVariant.DESTRUCTIVE else BadgeVariant.SUCCESS
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val varText = if (sel.variance >= 0) {
                                "$currencySymbol${String.format(Locale.US, "%.2f", sel.variance)} remaining"
                            } else {
                                "$currencySymbol${String.format(Locale.US, "%.2f", -sel.variance)} over cap"
                            }
                            Text(
                                text = varText,
                                color = if (sel.variance >= 0) AccentEmerald else AccentRose,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Legend Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Actual Spend", color = textMutedColor(), fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(AccentRose)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Set Monthly Limit", color = textMutedColor(), fontSize = 11.sp)
                }
            }
        }
    }
}
