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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryRegistry
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentIndigo
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentViolet
import com.example.ui.theme.CategoryColors
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800

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
 * Animated Donut Chart with center total and interactive touch glow.
 */
@Composable
fun DonutSpendChart(
    items: List<CategorySpendItem>,
    totalAmount: Double,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 900),
        label = "donut_anim"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val strokeWidth = 24.dp.toPx()
            val arcSize = size.width - strokeWidth
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            if (items.isEmpty() || totalAmount <= 0) {
                drawArc(
                    color = Color(0x22FFFFFF),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth)
                )
            } else {
                var startAngle = -90f
                for (item in items) {
                    val sweepAngle = (item.percentage / 100f) * 360f * animProgress
                    if (sweepAngle > 0f) {
                        drawArc(
                            color = item.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.coerceAtLeast(2f),
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(arcSize, arcSize),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spent",
                color = Slate400,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$currencySymbol${String.format(java.util.Locale.US, "%,.2f", totalAmount)}",
                color = Slate50,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

/**
 * Clean Shadcn-styled Spending Bar Chart.
 */
@Composable
fun BarSpendingChart(
    bars: List<BarChartItem>,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    if (bars.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No spending recorded for this period", color = Slate500, fontSize = 13.sp)
        }
        return
    }

    val maxAmount = (bars.maxOfOrNull { it.amount } ?: 100.0).coerceAtLeast(50.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            bars.forEach { bar ->
                val ratio = (bar.amount / maxAmount).toFloat().coerceIn(0.04f, 1f)
                val animatedRatio by animateFloatAsState(
                    targetValue = ratio,
                    animationSpec = tween(durationMillis = 650),
                    label = "bar_anim"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    if (bar.amount > 0) {
                        Text(
                            text = "$currencySymbol${bar.amount.toInt()}",
                            color = if (bar.isHighlighted) AccentCyan else Slate400,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Bar Column
                    Box(
                        modifier = Modifier
                            .width(22.dp)
                            .height((100 * animatedRatio).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                            .background(
                                if (bar.isHighlighted) {
                                    Brush.verticalGradient(listOf(AccentCyan, AccentIndigo))
                                } else if (bar.amount > 0) {
                                    Brush.verticalGradient(listOf(AccentViolet.copy(alpha = 0.8f), AccentIndigo.copy(alpha = 0.4f)))
                                } else {
                                    Brush.verticalGradient(listOf(Color(0x18FFFFFF), Color(0x08FFFFFF)))
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (bar.isHighlighted) AccentCyan.copy(alpha = 0.6f) else Color(0x20FFFFFF),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = bar.label,
                        color = if (bar.isHighlighted) Slate50 else Slate400,
                        fontSize = 11.sp,
                        fontWeight = if (bar.isHighlighted) FontWeight.Bold else FontWeight.Normal
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
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
