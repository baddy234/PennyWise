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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.FundVisualHelper
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.components.BadgeVariant
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundsScreen(
    funds: List<FundEntity>,
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    onCreateFundClick: () -> Unit,
    onEditFundClick: (FundEntity) -> Unit,
    onTopUpFundClick: (FundEntity) -> Unit,
    onDeleteFundClick: (FundEntity) -> Unit,
    onQuickCreatePreset: (name: String, balance: Double, target: Double?, colorHex: String, icon: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalFundsBalance = funds.sumOf { it.balance }
    val fundsWithTargets = funds.filter { it.targetAmount != null && it.targetAmount > 0 }
    val totalTargetAmount = fundsWithTargets.sumOf { it.targetAmount ?: 0.0 }
    val totalSavedTowardsTargets = fundsWithTargets.sumOf { it.balance.coerceAtLeast(0.0) }
    val overallTargetRatio = if (totalTargetAmount > 0) (totalSavedTowardsTargets / totalTargetAmount).toFloat().coerceIn(0f, 1f) else 0f

    var showPortfolioModal by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("funds_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Clean, Minimal Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Funds & Wallets",
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
                                    .background(AccentEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${funds.size} Active Pools • $currencySymbol${String.format(Locale.US, "%,.2f", totalFundsBalance)}",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    ShadcnBadge(
                        text = "Portfolio",
                        variant = BadgeVariant.SUCCESS
                    )
                }
            }

            // Main Content Action Bar: New Fund & Portfolio Breakdown
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Create New Fund Button
                    GlassButton(
                        text = "New Fund / Goal",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = onCreateFundClick,
                        gradient = Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("create_fund_fab")
                    )

                    // Portfolio Insights Modal Trigger Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate900.copy(alpha = 0.85f))
                            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                            .clickable { showPortfolioModal = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("funds_info_modal_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = "View Portfolio Breakdown",
                            tint = AccentCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Breakdown",
                            color = Slate200,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

        // Summary Hero Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Total Assets in Funds",
                                    color = Slate400,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${funds.size} Active ${if (funds.size == 1) "Fund" else "Funds"}",
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        ShadcnBadge(
                            text = if (totalFundsBalance >= 0) "Liquid Balance" else "Overdrawn",
                            variant = if (totalFundsBalance >= 0) BadgeVariant.SUCCESS else BadgeVariant.DESTRUCTIVE
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalFundsBalance)}",
                        color = if (totalFundsBalance >= 0) Slate50 else AccentRose,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    )

                    if (fundsWithTargets.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate900.copy(alpha = 0.7f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Savings Goals Progress",
                                        color = Slate300,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${(overallTargetRatio * 100).toInt()}% ($currencySymbol${String.format(Locale.US, "%,.0f", totalSavedTowardsTargets)} / $currencySymbol${String.format(Locale.US, "%,.0f", totalTargetAmount)})",
                                        color = AccentCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { overallTargetRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = AccentCyan,
                                    trackColor = Slate800
                                )
                            }
                        }
                    }
                }
            }
        }

        // Empty State if no funds
        if (funds.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AccentCyan.copy(alpha = 0.15f))
                                .border(1.dp, AccentCyan.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "No Funds Created Yet",
                            color = Slate100,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Create dedicated funds (e.g. Bank Account, Emergency Fund, Cash Stash) to link transactions and track balances automatically.",
                            color = Slate400,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Quick Start Starter Funds:",
                            color = Slate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                Triple("💳 Main Checking", 1000.0, "#38BDF8") to "Bank",
                                Triple("🛡️ Emergency Fund", 2500.0, "#34D399") to "Emergency",
                                Triple("✈️ Travel Vault", 500.0, "#FB7185") to "Travel",
                                Triple("💵 Cash Wallet", 150.0, "#FBBF24") to "Cash"
                            ).forEach { (fundInfo, icon) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate900)
                                        .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                        .clickable {
                                            val nameClean = fundInfo.first.substring(3)
                                            val target = if (icon == "Emergency") 5000.0 else if (icon == "Travel") 2000.0 else null
                                            onQuickCreatePreset(nameClean, fundInfo.second, target, fundInfo.third, icon)
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = fundInfo.first,
                                            color = Slate200,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "+ Start with $currencySymbol${fundInfo.second.toInt()}",
                                            color = AccentCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = AccentCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Funds Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Accounts & Funds",
                        color = Slate200,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${funds.size} total",
                        color = Slate400,
                        fontSize = 12.sp
                    )
                }
            }

            // Funds List
            items(funds, key = { it.id }) { fund ->
                FundCard(
                    fund = fund,
                    transactions = transactions.filter { it.fundId == fund.id },
                    currencySymbol = currencySymbol,
                    onTopUpClick = { onTopUpFundClick(fund) },
                    onEditClick = { onEditFundClick(fund) },
                    onDeleteClick = { onDeleteFundClick(fund) }
                )
            }
        }
    }

    // ==========================================
    // MODAL: FUNDS PORTFOLIO & GOALS BREAKDOWN
    // ==========================================
    if (showPortfolioModal) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showPortfolioModal = false },
                sheetState = sheetState,
                containerColor = DarkSurface,
                scrimColor = Color.Black.copy(alpha = 0.7f),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Slate700)
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
                                .background(AccentEmerald.copy(alpha = 0.2f))
                                .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = AccentEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Portfolio Asset Distribution",
                                color = Slate50,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp
                            )
                            Text(
                                text = "Breakdown of liquidity, savings goals & allocations",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Summary Stats Grid
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Total Combined Assets", color = Slate400, fontSize = 13.sp)
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalFundsBalance)}",
                                    color = Slate50,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Target Goals Total", color = Slate400, fontSize = 13.sp)
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalTargetAmount)}",
                                    color = AccentCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Goal Target Pace", color = Slate400, fontSize = 13.sp)
                                Text(
                                    text = "${(overallTargetRatio * 100).toInt()}% Met",
                                    color = if (overallTargetRatio >= 0.8f) AccentEmerald else Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Per-Fund Breakdown List
                    Text(
                        text = "ACCOUNTS & POOLS (${funds.size})",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    funds.forEach { fund ->
                        val color = FundVisualHelper.getColor(fund.colorHex)
                        val share = if (totalFundsBalance > 0) (fund.balance.coerceAtLeast(0.0) / totalFundsBalance) * 100 else 0.0

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate900.copy(alpha = 0.5f))
                                .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = fund.name,
                                    color = Slate200,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$currencySymbol${String.format(Locale.US, "%,.2f", fund.balance)}",
                                    color = Slate50,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                ShadcnBadge(
                                    text = "${share.toInt()}%",
                                    variant = BadgeVariant.SECONDARY
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    GlassButton(
                        text = "Close Breakdown",
                        onClick = { showPortfolioModal = false },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun FundCard(
    fund: FundEntity,
    transactions: List<TransactionEntity>,
    currencySymbol: String,
    onTopUpClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val fundColor = FundVisualHelper.getColor(fund.colorHex)
    val fundIcon = FundVisualHelper.getIcon(fund.iconName)

    val hasTarget = fund.targetAmount != null && fund.targetAmount > 0
    val targetRatio = if (hasTarget) (fund.balance / fund.targetAmount!!).toFloat().coerceIn(0f, 1f) else 0f
    val targetPercent = (targetRatio * 100).toInt()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("fund_card_${fund.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(fundColor.copy(alpha = 0.2f))
                            .border(1.dp, fundColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = fundIcon,
                            contentDescription = null,
                            tint = fundColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = fund.name,
                            color = Slate50,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (fund.note.isNotBlank()) {
                            Text(
                                text = fund.note,
                                color = Slate400,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        } else {
                            Text(
                                text = "${transactions.size} linked transactions",
                                color = Slate500,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add Funds / Deposit", color = AccentEmerald, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onTopUpClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Fund Details", color = Slate200, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Slate300,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Fund", color = AccentRose, fontSize = 13.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = AccentRose,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balance Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Available Balance",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.2f", fund.balance)}",
                        color = if (fund.balance >= 0) Slate50 else AccentRose,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Quick Top Up Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentEmerald.copy(alpha = 0.18f))
                        .border(1.dp, AccentEmerald.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .clickable { onTopUpClick() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add Funds",
                            color = AccentEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Target Goal Progress if configured
            if (hasTarget) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Slate900.copy(alpha = 0.6f))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Goal: $currencySymbol${String.format(Locale.US, "%,.2f", fund.targetAmount!!)}",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$targetPercent% reached",
                            color = fundColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { targetRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = fundColor,
                        trackColor = Slate800
                    )
                }
            }
        }
    }
}
