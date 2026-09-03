package com.example.ui.modals

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsModal(
    transactions: List<TransactionEntity>,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onAddRecurringClick: () -> Unit,
    onPostPaymentNow: (TransactionEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val recurringList = transactions.filter { it.isRecurring }
    val totalMonthlyFixedCost = recurringList.sumOf { item ->
        when (item.recurringInterval.uppercase(Locale.US)) {
            "WEEKLY" -> item.amount * 4.33
            "YEARLY" -> item.amount / 12.0
            else -> item.amount // MONTHLY
        }
    }

    val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentIndigo.copy(alpha = 0.2f))
                            .border(1.dp, AccentIndigo.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventRepeat,
                            contentDescription = null,
                            tint = AccentIndigo,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Subscriptions & Recurring",
                            color = textPrimaryColor(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Fixed bills, memberships & automated charges",
                            color = textMutedColor(),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Monthly Fixed Cost Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0x356366F1),
                                Color(0x1538BDF8),
                                Color(0x250F172A)
                            )
                        )
                    )
                    .border(1.dp, AccentIndigo.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ESTIMATED MONTHLY FIXED COST",
                            color = textMutedColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalMonthlyFixedCost)}",
                            color = textPrimaryColor(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    ShadcnBadge(
                        text = "${recurringList.size} Active",
                        variant = BadgeVariant.CYAN
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE RECURRING BILLS",
                    color = textMutedColor(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "+ Add Recurring",
                    color = AccentCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onDismiss()
                            onAddRecurringClick()
                        }
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                        .testTag("add_recurring_button")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (recurringList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = textMutedColor(),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active recurring bills yet",
                            color = textMutedColor(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Mark expenses as recurring when recording transactions",
                            color = textMutedColor().copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(recurringList) { item ->
                        val cat = CategoryRegistry.getCategory(item.category)
                        val catIcon = cat.icon
                        val catColor = cat.color

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(glassSurfaceBgColor())
                                .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(catColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = catIcon,
                                        contentDescription = null,
                                        tint = catColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        color = textPrimaryColor(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${item.recurringInterval.lowercase().replaceFirstChar { it.uppercase() }} • ",
                                            color = AccentCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = item.fundName ?: "Main Balance",
                                            color = textMutedColor(),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%.2f", item.amount)}",
                                    color = textPrimaryColor(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AccentEmerald.copy(alpha = 0.2f))
                                        .border(1.dp, AccentEmerald.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            onPostPaymentNow(item)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                        .testTag("post_recurring_${item.id}")
                                ) {
                                    Text(
                                        text = "Post Payment",
                                        color = AccentEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                text = "Close",
                onClick = onDismiss,
                isSecondary = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
