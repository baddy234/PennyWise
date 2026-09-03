package com.example.ui.modals

import com.example.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.DebtEntity
import com.example.ui.components.GlassButton
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate900
import java.util.Locale

import com.example.ui.theme.appTextFieldColors
import com.example.ui.theme.glassBorderColor
import com.example.ui.theme.glassModalContainerColor
import com.example.ui.theme.glassSurfaceBgColor
import com.example.ui.theme.textMutedColor
import com.example.ui.theme.textPrimaryColor
import com.example.ui.theme.textSecondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDebtPaymentModal(
    debt: DebtEntity,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onRecordPayment: (paymentAmount: Double, recordAsTransaction: Boolean) -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val remaining = (debt.totalAmount - debt.amountPaid).coerceAtLeast(0.0)
    var paymentAmountText by remember { mutableStateOf(String.format(Locale.US, "%.2f", remaining)) }
    var recordAsTransaction by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isOwedToYou = debt.type == "OWED_TO_YOU"

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = glassModalContainerColor(),
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isOwedToYou) "RECORD REPAYMENT" else "RECORD DEBT PAYMENT",
                        color = textMutedColor(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = debt.personName,
                        color = textPrimaryColor(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textSecondaryColor()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Debt Summary info box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Record:", color = textMutedColor(), fontSize = 12.sp)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", debt.totalAmount)}",
                        color = textPrimaryColor(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Already Paid:", color = textMutedColor(), fontSize = 12.sp)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", debt.amountPaid)}",
                        color = AccentEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Remaining Balance:", color = textSecondaryColor(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$currencySymbol${String.format(Locale.US, "%.2f", remaining)}",
                        color = if (isOwedToYou) AccentEmerald else AccentRose,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Input
            Text(
                text = "Payment Amount ($currencySymbol)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = paymentAmountText,
                onValueChange = { paymentAmountText = it },
                placeholder = { Text("0.00", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$currencySymbol ", color = AccentCyan, fontWeight = FontWeight.Bold) },
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("record_payment_amount_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Checkbox: Record as Transaction
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(glassSurfaceBgColor())
                    .clickable { recordAsTransaction = !recordAsTransaction }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = recordAsTransaction,
                    onCheckedChange = { recordAsTransaction = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AccentCyan,
                        checkmarkColor = Color.Black
                    )
                )
                Text(
                    text = if (isOwedToYou) "Add to Cashflow as Income (+${currencySymbol})" else "Add to Cashflow as Expense (-${currencySymbol})",
                    color = textPrimaryColor(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            errorMessage?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = err, color = AccentRose, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            GlassButton(
                text = "Confirm Payment",
                onClick = {
                    val pAmount = paymentAmountText.toDoubleOrNull()
                    if (pAmount == null || pAmount <= 0) {
                        errorMessage = "Please enter a valid payment amount"
                    } else {
                        onRecordPayment(pAmount, recordAsTransaction)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("confirm_debt_payment_btn")
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
