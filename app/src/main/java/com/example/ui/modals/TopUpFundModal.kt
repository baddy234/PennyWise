package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FundEntity
import com.example.data.model.FundVisualHelper
import com.example.ui.components.GlassButton
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TopUpFundModal(
    fund: FundEntity,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onConfirmTopUp: (amount: Double, note: String, recordAsIncome: Boolean) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var recordAsIncome by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val quickAddAmounts = listOf(10.0, 25.0, 50.0, 100.0, 250.0, 500.0)
    val fundColor = FundVisualHelper.getColor(fund.colorHex)

    val currentBal = fund.balance
    val addedAmount = amountStr.toDoubleOrNull() ?: 0.0
    val projectedBalance = currentBal + addedAmount

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
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(fundColor.copy(alpha = 0.2f))
                        .border(1.dp, fundColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = FundVisualHelper.getIcon(fund.iconName),
                        contentDescription = null,
                        tint = fundColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Add Funds",
                        color = textPrimaryColor(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.4).sp
                    )
                    Text(
                        text = "Deposit to ${fund.name}",
                        color = textMutedColor(),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Balance Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Balance",
                            color = textMutedColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%,.2f", currentBal)}",
                            color = textSecondaryColor(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(20.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Projected Balance",
                            color = AccentEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$currencySymbol${String.format(Locale.US, "%,.2f", projectedBalance)}",
                            color = AccentEmerald,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Input
            OutlinedTextField(
                value = amountStr,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        amountStr = it
                    }
                },
                label = { Text("Deposit Amount", color = textMutedColor()) },
                placeholder = { Text("0.00", color = textMutedColor().copy(alpha = 0.6f)) },
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        color = AccentEmerald,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = appTextFieldColors(focusedBorderColor = AccentEmerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_up_amount_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Add Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickAddAmounts.forEach { chipAmt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(glassSurfaceBgColor())
                            .border(1.dp, glassBorderColor(), RoundedCornerShape(10.dp))
                            .clickable {
                                val current = amountStr.toDoubleOrNull() ?: 0.0
                                amountStr = String.format(Locale.US, "%.2f", current + chipAmt)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "+$currencySymbol${chipAmt.toInt()}",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note (Optional)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Memo / Source (Optional)", color = textMutedColor()) },
                placeholder = { Text("e.g. Paycheck deposit, bonus, transfer", color = textMutedColor().copy(alpha = 0.6f)) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Record as Income Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { recordAsIncome = !recordAsIncome }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = recordAsIncome,
                    onCheckedChange = { recordAsIncome = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AccentEmerald,
                        uncheckedColor = textMutedColor(),
                        checkmarkColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Log as Income transaction in history",
                        color = textSecondaryColor(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Adds an entry to your transactions log for accurate reports",
                        color = textMutedColor(),
                        fontSize = 11.sp
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = AccentRose,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Button
            GlassButton(
                text = "Deposit $currencySymbol${if (addedAmount > 0) String.format(Locale.US, "%.2f", addedAmount) else "0.00"} into ${fund.name}",
                onClick = {
                    if (addedAmount <= 0) {
                        errorMessage = "Please enter an amount greater than 0."
                        return@GlassButton
                    }
                    errorMessage = null
                    onConfirmTopUp(addedAmount, note.trim(), recordAsIncome)
                    onDismiss()
                },
                gradient = Brush.horizontalGradient(listOf(AccentEmerald, Color(0xFF059669))),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_top_up_button")
            )
        }
    }
}
