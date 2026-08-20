package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.CurrencyRate
import com.example.data.remote.CurrencyRepository
import com.example.ui.components.AmbientGlassBackground
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
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

enum class ToolsSubTab(val title: String, val icon: ImageVector) {
    INTEREST("Interest Calc", Icons.Default.Calculate),
    SAVINGS("Goal & Savings", Icons.Default.Savings),
    CURRENCY("Currency Rates", Icons.Default.CurrencyExchange)
}

@Composable
fun ToolsScreen(
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ToolsSubTab.INTEREST) }

    AmbientGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FINANCIAL TOOLKIT",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Smart Calculators",
                        color = Slate50,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                }
                ShadcnBadge(text = "Pro Tools", variant = BadgeVariant.CYAN)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-Tab Switcher Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurfaceDark)
                    .border(1.dp, GlassBorderDark, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToolsSubTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (isSelected) Modifier.background(
                                    Brush.horizontalGradient(listOf(AccentIndigo, AccentViolet))
                                ) else Modifier.background(Color.Transparent)
                            )
                            .clickable { selectedTab = tab }
                            .testTag("tools_tab_${tab.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) Color.White else Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                color = if (isSelected) Color.White else Slate300,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    ToolsSubTab.INTEREST -> InterestCalculatorView(currencySymbol = currencySymbol)
                    ToolsSubTab.SAVINGS -> SavingsGoalToolView(currencySymbol = currencySymbol)
                    ToolsSubTab.CURRENCY -> CurrencyConverterView(currencySymbol = currencySymbol)
                }
            }
        }
    }
}

// ==========================================
// 1. INTEREST CALCULATOR TAB
// ==========================================
@Composable
private fun InterestCalculatorView(currencySymbol: String) {
    var principalInput by remember { mutableStateOf("10000") }
    var rateInput by remember { mutableStateOf("7.5") }
    var periodInput by remember { mutableStateOf("5") }
    var isPeriodInYears by remember { mutableStateOf(true) }
    var isCompound by remember { mutableStateOf(true) }

    // Frequency options: Annual (1), Semi-Annual (2), Quarterly (4), Monthly (12), Daily (365)
    var compoundFreqName by remember { mutableStateOf("Monthly") }
    var freqExpanded by remember { mutableStateOf(false) }

    val freqTimes = when (compoundFreqName) {
        "Annually" -> 1
        "Semi-Annually" -> 2
        "Quarterly" -> 4
        "Daily" -> 365
        else -> 12 // Monthly
    }

    val principal = principalInput.toDoubleOrNull() ?: 0.0
    val rate = (rateInput.toDoubleOrNull() ?: 0.0) / 100.0
    val rawPeriod = periodInput.toDoubleOrNull() ?: 0.0
    val years = if (isPeriodInYears) rawPeriod else rawPeriod / 12.0

    val (totalAmount, totalInterest) = remember(principal, rate, years, isCompound, freqTimes) {
        if (principal <= 0.0 || years <= 0.0) {
            Pair(principal, 0.0)
        } else if (!isCompound) {
            // Simple Interest: I = P * r * t
            val interest = principal * rate * years
            Pair(principal + interest, interest)
        } else {
            // Compound Interest: A = P * (1 + r/n)^(n*t)
            val n = freqTimes.toDouble()
            val finalVal = principal * (1 + rate / n).pow(n * years)
            Pair(finalVal, finalVal - principal)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Interest Type",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCompound) AccentIndigo.copy(alpha = 0.2f) else Slate900)
                                .border(
                                    1.dp,
                                    if (isCompound) AccentIndigo else Slate800,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { isCompound = true }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = isCompound,
                                onClick = { isCompound = true },
                                colors = RadioButtonDefaults.colors(selectedColor = AccentIndigo)
                            )
                            Text("Compound", color = Slate50, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isCompound) AccentCyan.copy(alpha = 0.2f) else Slate900)
                                .border(
                                    1.dp,
                                    if (!isCompound) AccentCyan else Slate800,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { isCompound = false }
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = !isCompound,
                                onClick = { isCompound = false },
                                colors = RadioButtonDefaults.colors(selectedColor = AccentCyan)
                            )
                            Text("Simple", color = Slate50, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Fields
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = principalInput,
                            onValueChange = { principalInput = it },
                            label = { Text("Principal ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentIndigo,
                                unfocusedBorderColor = Slate700,
                                focusedLabelColor = AccentIndigo,
                                unfocusedLabelColor = Slate400,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate100
                            )
                        )

                        OutlinedTextField(
                            value = rateInput,
                            onValueChange = { rateInput = it },
                            label = { Text("Annual Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentIndigo,
                                unfocusedBorderColor = Slate700,
                                focusedLabelColor = AccentIndigo,
                                unfocusedLabelColor = Slate400,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate100
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = periodInput,
                            onValueChange = { periodInput = it },
                            label = { Text(if (isPeriodInYears) "Period (Years)" else "Period (Months)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentIndigo,
                                unfocusedBorderColor = Slate700,
                                focusedLabelColor = AccentIndigo,
                                unfocusedLabelColor = Slate400,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate100
                            )
                        )

                        // Toggle Years/Months
                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate800)
                                .clickable { isPeriodInYears = !isPeriodInYears }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isPeriodInYears) "Years 🔄" else "Months 🔄",
                                color = AccentCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isCompound) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate900)
                                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                    .clickable { freqExpanded = true }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Compounding: $compoundFreqName", color = Slate200, fontSize = 13.sp)
                                    Text("▼", color = Slate400, fontSize = 11.sp)
                                }
                            }

                            DropdownMenu(
                                expanded = freqExpanded,
                                onDismissRequest = { freqExpanded = false },
                                modifier = Modifier.background(Slate900)
                            ) {
                                listOf("Monthly", "Quarterly", "Semi-Annually", "Annually", "Daily").forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option, color = Slate100) },
                                        onClick = {
                                            compoundFreqName = option
                                            freqExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Summary Infographic Output
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlowColor = AccentEmerald
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CALCULATED GROWTH",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalAmount)}",
                        color = AccentEmerald,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Visual Progress Split Gauge
                    val interestPct = if (totalAmount > 0) (totalInterest / totalAmount).toFloat().coerceIn(0f, 1f) else 0f
                    val principalPct = 1f - interestPct

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                    ) {
                        if (principalPct > 0f) {
                            Box(
                                modifier = Modifier
                                    .weight(principalPct)
                                    .height(12.dp)
                                    .background(AccentIndigo)
                            )
                        }
                        if (interestPct > 0f) {
                            Box(
                                modifier = Modifier
                                    .weight(interestPct)
                                    .height(12.dp)
                                    .background(AccentEmerald)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentIndigo)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Initial Principal", color = Slate400, fontSize = 11.sp)
                            }
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", principal)}",
                                color = Slate50,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Total Interest Earned", color = Slate400, fontSize = 11.sp)
                            }
                            Text(
                                text = "+$currencySymbol${String.format(Locale.US, "%,.2f", totalInterest)}",
                                color = AccentEmerald,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ==========================================
// 2. SAVINGS & GOAL CALCULATOR TAB
// ==========================================
@Composable
private fun SavingsGoalToolView(currencySymbol: String) {
    // Mode 0 = Find Time needed for target
    // Mode 1 = Find Total accumulated from timeframe & deposits
    // Mode 2 = Find Required deposit for target & timeframe
    var goalMode by remember { mutableIntStateOf(0) }

    var targetAmountInput by remember { mutableStateOf("12000") }
    var depositAmountInput by remember { mutableStateOf("500") }
    var timeframeInput by remember { mutableStateOf("24") } // Months
    var selectedFreq by remember { mutableStateOf("Monthly") } // Daily, Weekly, Monthly

    val freqMultiplier = when (selectedFreq) {
        "Daily" -> 30.0
        "Weekly" -> 4.33
        else -> 1.0 // Monthly
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Calculation Goal Mode",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            0 to "Time Needed",
                            1 to "Total Value",
                            2 to "Deposit Req."
                        ).forEach { (mode, label) ->
                            val isSel = goalMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) AccentIndigo else Slate900)
                                    .clickable { goalMode = mode },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else Slate300,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Frequency selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Deposit Frequency:", color = Slate300, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Daily", "Weekly", "Monthly").forEach { freq ->
                                val isSel = selectedFreq == freq
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) AccentCyan.copy(alpha = 0.2f) else Slate900)
                                        .border(1.dp, if (isSel) AccentCyan else Slate800, RoundedCornerShape(8.dp))
                                        .clickable { selectedFreq = freq }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = freq,
                                        color = if (isSel) AccentCyan else Slate400,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dynamic Input Fields based on Mode
                    when (goalMode) {
                        0 -> { // Time needed
                            OutlinedTextField(
                                value = targetAmountInput,
                                onValueChange = { targetAmountInput = it },
                                label = { Text("Target Goal Amount ($currencySymbol)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = depositAmountInput,
                                onValueChange = { depositAmountInput = it },
                                label = { Text("Regular $selectedFreq Deposit ($currencySymbol)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                        }

                        1 -> { // Total Value
                            OutlinedTextField(
                                value = depositAmountInput,
                                onValueChange = { depositAmountInput = it },
                                label = { Text("Regular $selectedFreq Deposit ($currencySymbol)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = timeframeInput,
                                onValueChange = { timeframeInput = it },
                                label = { Text("Total Timeframe (Months)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                        }

                        2 -> { // Required Deposit
                            OutlinedTextField(
                                value = targetAmountInput,
                                onValueChange = { targetAmountInput = it },
                                label = { Text("Target Goal Amount ($currencySymbol)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = timeframeInput,
                                onValueChange = { timeframeInput = it },
                                label = { Text("Total Timeframe (Months)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentIndigo,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                )
                            )
                        }
                    }
                }
            }
        }

        // Calculation Results Card
        item {
            val target = targetAmountInput.toDoubleOrNull() ?: 0.0
            val deposit = depositAmountInput.toDoubleOrNull() ?: 0.0
            val months = timeframeInput.toDoubleOrNull() ?: 0.0

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlowColor = AccentCyan
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (goalMode) {
                        0 -> { // Time Needed
                            val monthlyDeposit = deposit * freqMultiplier
                            val totalMonthsNeeded = if (monthlyDeposit > 0) target / monthlyDeposit else 0.0
                            val yearsNeeded = totalMonthsNeeded / 12.0

                            Text("REQUIRED TIMEFRAME", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (totalMonthsNeeded > 0) {
                                    String.format(Locale.US, "%.1f Months (%.1f Yrs)", totalMonthsNeeded, yearsNeeded)
                                } else "Enter valid deposit amount",
                                color = AccentCyan,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "To reach $currencySymbol${String.format(Locale.US, "%,.2f", target)} with $currencySymbol${String.format(Locale.US, "%,.2f", deposit)} $selectedFreq deposits.",
                                color = Slate300,
                                fontSize = 12.sp
                            )
                        }

                        1 -> { // Total Accumulated
                            val monthlyDeposit = deposit * freqMultiplier
                            val totalVal = monthlyDeposit * months

                            Text("TOTAL ACCUMULATED SAVINGS", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", totalVal)}",
                                color = AccentEmerald,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Accumulated over ${months.toInt()} months depositing $currencySymbol${String.format(Locale.US, "%,.2f", deposit)} $selectedFreq.",
                                color = Slate300,
                                fontSize = 12.sp
                            )
                        }

                        2 -> { // Required Deposit
                            val totalMonthlyReq = if (months > 0) target / months else 0.0
                            val reqDeposit = totalMonthlyReq / freqMultiplier

                            Text("REQUIRED $selectedFreq DEPOSIT", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%,.2f", reqDeposit)} / $selectedFreq",
                                color = AccentRose,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Needed to reach $currencySymbol${String.format(Locale.US, "%,.2f", target)} in ${months.toInt()} months.",
                                color = Slate300,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ==========================================
// 3. CURRENCY CONVERTER TAB
// ==========================================
@Composable
private fun CurrencyConverterView(currencySymbol: String) {
    var sourceCode by remember { mutableStateOf("USD") }
    var targetCode by remember { mutableStateOf("EUR") }
    var amountInput by remember { mutableStateOf("100") }

    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var ratesMap by remember { mutableStateOf(CurrencyRepository.getRates()) }

    var sourceExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        ratesMap = CurrencyRepository.getRates()
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val convertedValue = remember(amount, sourceCode, targetCode, ratesMap) {
        CurrencyRepository.convert(amount, sourceCode, targetCode)
    }

    val sourceRate = ratesMap[sourceCode]?.rateVsUsd ?: 1.0
    val targetRate = ratesMap[targetCode]?.rateVsUsd ?: 1.0
    val directRate = if (sourceRate > 0) targetRate / sourceRate else 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE EXCHANGE CONVERTER",
                            color = Slate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )

                        // Refresh button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    isRefreshing = true
                                    CurrencyRepository.fetchLatestRates()
                                    ratesMap = CurrencyRepository.getRates()
                                    isRefreshing = false
                                }
                            },
                            enabled = !isRefreshing
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = AccentCyan,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Rates",
                                    tint = AccentCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Amount Input
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount to Convert") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Currency Selector Pair
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Source Selector
                        Box(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate900)
                                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                    .clickable { sourceExpanded = true }
                                    .padding(12.dp)
                            ) {
                                val meta = ratesMap[sourceCode]
                                Column {
                                    Text("From", color = Slate400, fontSize = 10.sp)
                                    Text(
                                        text = "${meta?.code ?: sourceCode} (${meta?.symbol ?: ""})",
                                        color = Slate50,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = sourceExpanded,
                                onDismissRequest = { sourceExpanded = false },
                                modifier = Modifier.background(Slate900)
                            ) {
                                ratesMap.values.forEach { rateMeta ->
                                    DropdownMenuItem(
                                        text = { Text("${rateMeta.code} - ${rateMeta.name}", color = Slate100) },
                                        onClick = {
                                            sourceCode = rateMeta.code
                                            sourceExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Swap Icon Button
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AccentIndigo.copy(alpha = 0.3f))
                                .border(1.dp, AccentIndigo, CircleShape)
                                .clickable {
                                    val tmp = sourceCode
                                    sourceCode = targetCode
                                    targetCode = tmp
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Swap Currencies",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        // Target Selector
                        Box(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate900)
                                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                    .clickable { targetExpanded = true }
                                    .padding(12.dp)
                            ) {
                                val meta = ratesMap[targetCode]
                                Column {
                                    Text("To", color = Slate400, fontSize = 10.sp)
                                    Text(
                                        text = "${meta?.code ?: targetCode} (${meta?.symbol ?: ""})",
                                        color = Slate50,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = targetExpanded,
                                onDismissRequest = { targetExpanded = false },
                                modifier = Modifier.background(Slate900)
                            ) {
                                ratesMap.values.forEach { rateMeta ->
                                    DropdownMenuItem(
                                        text = { Text("${rateMeta.code} - ${rateMeta.name}", color = Slate100) },
                                        onClick = {
                                            targetCode = rateMeta.code
                                            targetExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Conversion Result Card
        item {
            val targetMeta = ratesMap[targetCode]
            val sourceMeta = ratesMap[sourceCode]

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderGlowColor = AccentCyan
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CONVERTED VALUE",
                        color = Slate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${targetMeta?.symbol ?: ""}${String.format(Locale.US, "%,.2f", convertedValue)} ${targetCode}",
                        color = AccentCyan,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1 $sourceCode = ${String.format(Locale.US, "%.4f", directRate)} $targetCode",
                        color = Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val timeStr = SimpleDateFormat("HH:mm, MMM d", Locale.getDefault())
                        .format(Date(CurrencyRepository.lastUpdatedTime))
                    Text(
                        text = "Exchange rates updated as of $timeStr",
                        color = Slate400,
                        fontSize = 10.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
