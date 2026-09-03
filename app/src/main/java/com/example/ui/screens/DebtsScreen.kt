package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DebtEntity
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DebtFilterTab {
    ALL,
    OWED_TO_ME,
    I_OWE,
    SETTLED
}

@Composable
fun DebtsScreen(
    debts: List<DebtEntity>,
    currencySymbol: String,
    onCreateDebtClick: () -> Unit,
    onEditDebtClick: (DebtEntity) -> Unit,
    onRecordPaymentClick: (DebtEntity) -> Unit,
    onToggleSettledClick: (DebtEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(DebtFilterTab.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val owedToYouTotal = remember(debts) {
        debts.filter { it.type == "OWED_TO_YOU" && !it.isSettled }
            .sumOf { (it.totalAmount - it.amountPaid).coerceAtLeast(0.0) }
    }

    val youOweTotal = remember(debts) {
        debts.filter { it.type == "YOU_OWE" && !it.isSettled }
            .sumOf { (it.totalAmount - it.amountPaid).coerceAtLeast(0.0) }
    }

    val netPosition = owedToYouTotal - youOweTotal

    val filteredDebts = remember(debts, selectedTab, searchQuery) {
        debts.filter { debt ->
            val matchesTab = when (selectedTab) {
                DebtFilterTab.ALL -> !debt.isSettled
                DebtFilterTab.OWED_TO_ME -> debt.type == "OWED_TO_YOU" && !debt.isSettled
                DebtFilterTab.I_OWE -> debt.type == "YOU_OWE" && !debt.isSettled
                DebtFilterTab.SETTLED -> debt.isSettled
            }
            val matchesSearch = searchQuery.isBlank() ||
                debt.personName.contains(searchQuery, ignoreCase = true) ||
                debt.notes.contains(searchQuery, ignoreCase = true)

            matchesTab && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DEBTS & DEBTORS",
                    color = textMutedColor(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Loans & Receivables",
                    color = textPrimaryColor(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            GlassButton(
                text = "+ Add Record",
                onClick = onCreateDebtClick,
                modifier = Modifier.testTag("add_debt_record_btn")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Financial Position Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Owed To You (Receivables)
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentEmerald.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "OWED TO YOU", color = textMutedColor(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", owedToYouTotal)}",
                        color = AccentEmerald,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // You Owe (Payables)
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentRose.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "YOU OWE", color = textMutedColor(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", youOweTotal)}",
                        color = AccentRose,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Net Balance
            GlassCard(
                modifier = Modifier.weight(1f),
                borderGlowColor = AccentCyan.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "NET POSITION", color = textMutedColor(), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${if (netPosition >= 0) "+" else ""}$currencySymbol${String.format(Locale.US, "%.2f", netPosition)}",
                        color = if (netPosition >= 0) AccentCyan else AccentRose,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Filter Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(glassSurfaceBgColor())
                .border(1.dp, glassBorderColor(), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                DebtFilterTab.ALL to "Active",
                DebtFilterTab.OWED_TO_ME to "Owed to Me",
                DebtFilterTab.I_OWE to "I Owe",
                DebtFilterTab.SETTLED to "Settled"
            )

            tabs.forEach { (tab, label) ->
                val isSel = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) AccentCyan else Color.Transparent)
                        .clickable { selectedTab = tab }
                        .padding(vertical = 9.dp)
                        .testTag("debt_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSel) Color.Black else textSecondaryColor(),
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.ExtraBold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name or note...", color = textMutedColor(), fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = textMutedColor()) },
            singleLine = true,
            colors = appTextFieldColors(focusedBorderColor = AccentCyan),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Debt Records List
        if (filteredDebts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == DebtFilterTab.SETTLED) "No Settled Records" else "No Debt Records",
                        color = Slate300,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap '+ Add Record' to keep track of loans and debts",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredDebts, key = { it.id }) { debt ->
                    DebtCardItem(
                        debt = debt,
                        currencySymbol = currencySymbol,
                        onRecordPayment = { onRecordPaymentClick(debt) },
                        onToggleSettled = { onToggleSettledClick(debt) },
                        onEdit = { onEditDebtClick(debt) },
                        onCallPhone = { phone ->
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phone")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DebtCardItem(
    debt: DebtEntity,
    currencySymbol: String,
    onRecordPayment: () -> Unit,
    onToggleSettled: () -> Unit,
    onEdit: () -> Unit,
    onCallPhone: (String) -> Unit
) {
    val isOwedToYou = debt.type == "OWED_TO_YOU"
    val remaining = (debt.totalAmount - debt.amountPaid).coerceAtLeast(0.0)
    val progressRatio = if (debt.totalAmount > 0) (debt.amountPaid / debt.totalAmount).coerceIn(0.0, 1.0).toFloat() else 0f

    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    val accentColor = when {
        debt.isSettled -> Slate400
        isOwedToYou -> AccentEmerald
        else -> AccentRose
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderGlowColor = accentColor.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Card Header
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
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isOwedToYou) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = debt.personName,
                            color = textPrimaryColor(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ShadcnBadge(
                                text = if (isOwedToYou) "OWED TO YOU" else "YOU OWE",
                                variant = if (isOwedToYou) BadgeVariant.SUCCESS else BadgeVariant.DESTRUCTIVE
                            )
                            if (debt.isSettled) {
                                ShadcnBadge(text = "SETTLED", variant = BadgeVariant.SECONDARY)
                            }
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", remaining)}",
                        color = accentColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Total: $currencySymbol${String.format(Locale.US, "%.2f", debt.totalAmount)}",
                        color = textMutedColor(),
                        fontSize = 11.sp
                    )
                }
            }

            // Progress bar
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = if (isAppInDarkTheme()) Slate800 else Color(0xFFCBD5E1)
            )

            if (debt.notes.isNotBlank() || debt.dueDateMillis != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = debt.notes.ifBlank { "No additional notes" },
                        color = textMutedColor(),
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f)
                    )

                    debt.dueDateMillis?.let { dueMs ->
                        Text(
                            text = "Due: ${dateFormat.format(Date(dueMs))}",
                            color = textSecondaryColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Contact phone call button if available
                    if (debt.contactPhone.isNotBlank()) {
                        IconButton(
                            onClick = { onCallPhone(debt.contactPhone) },
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(glassSurfaceBgColor())
                                .border(1.dp, glassBorderColor(), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call Contact",
                                tint = AccentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(glassSurfaceBgColor())
                            .border(1.dp, glassBorderColor(), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Record",
                            tint = textSecondaryColor(),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Toggle Settled Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (debt.isSettled) Slate800 else GlassSurfaceDark)
                            .border(1.dp, GlassBorderDark, RoundedCornerShape(10.dp))
                            .clickable { onToggleSettled() }
                            .padding(horizontal = 10.dp, vertical = 7.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (debt.isSettled) Icons.Default.CheckCircle else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (debt.isSettled) AccentEmerald else Slate400,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (debt.isSettled) "Settled" else "Mark Paid",
                                color = if (debt.isSettled) AccentEmerald else Slate300,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Record Payment Button
                    if (!debt.isSettled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(AccentEmerald, AccentCyan)
                                    )
                                )
                                .clickable { onRecordPayment() }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Pay / Settle",
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
}
