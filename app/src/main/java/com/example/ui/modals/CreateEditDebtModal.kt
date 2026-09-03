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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditDebtModal(
    debtToEdit: DebtEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        personName: String,
        type: String,
        totalAmount: Double,
        amountPaid: Double,
        dueDateMillis: Long?,
        notes: String,
        contactPhone: String
    ) -> Unit,
    onDelete: ((DebtEntity) -> Unit)? = null
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var personName by remember { mutableStateOf(debtToEdit?.personName ?: "") }
    var type by remember { mutableStateOf(debtToEdit?.type ?: "OWED_TO_YOU") } // OWED_TO_YOU vs YOU_OWE
    var totalAmountText by remember { mutableStateOf(debtToEdit?.totalAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var amountPaidText by remember { mutableStateOf(debtToEdit?.amountPaid?.let { String.format(Locale.US, "%.2f", it) } ?: "0.00") }
    var notes by remember { mutableStateOf(debtToEdit?.notes ?: "") }
    var contactPhone by remember { mutableStateOf(debtToEdit?.contactPhone ?: "") }
    var daysUntilDue by remember { mutableStateOf("14") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                .verticalScroll(rememberScrollState())
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
                        text = if (debtToEdit == null) "NEW RECORD" else "EDIT RECORD",
                        color = textMutedColor(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (debtToEdit == null) "Add Debt or Loan" else "Update Record",
                        color = textPrimaryColor(),
                        fontSize = 22.sp,
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

            Spacer(modifier = Modifier.height(18.dp))

            // Debt Type Selector Tabs
            Text(
                text = "Record Category",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 1: Someone Owes Me (Debtor)
                val isOwed = type == "OWED_TO_YOU"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isOwed) AccentEmerald else Color.Transparent)
                        .clickable { type = "OWED_TO_YOU" }
                        .padding(vertical = 12.dp)
                        .testTag("debt_type_owed_to_me"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isOwed) Color.Black else textSecondaryColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Owed to Me",
                            color = if (isOwed) Color.Black else textSecondaryColor(),
                            fontSize = 13.sp,
                            fontWeight = if (isOwed) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }

                // Tab 2: I Owe Someone (Creditor / Debt)
                val isIOwe = type == "YOU_OWE"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isIOwe) AccentRose else Color.Transparent)
                        .clickable { type = "YOU_OWE" }
                        .padding(vertical = 12.dp)
                        .testTag("debt_type_i_owe"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIOwe) Color.White else textSecondaryColor(),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "I Owe Someone",
                            color = if (isIOwe) Color.White else textSecondaryColor(),
                            fontSize = 13.sp,
                            fontWeight = if (isIOwe) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Person Name Field
            Text(
                text = if (type == "OWED_TO_YOU") "Debtor / Borrower Name" else "Creditor / Person You Owe",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                placeholder = { Text(if (type == "OWED_TO_YOU") "e.g. John Smith, Sarah" else "e.g. Bank, Alex, Landlord", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_person_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Total Amount Field
            Text(
                text = "Total Amount ($currencySymbol)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = totalAmountText,
                onValueChange = { totalAmountText = it },
                placeholder = { Text("0.00", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$currencySymbol ", color = AccentCyan, fontWeight = FontWeight.Bold) },
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_total_amount_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Already Paid / Received
            Text(
                text = "Amount Already Settled ($currencySymbol)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountPaidText,
                onValueChange = { amountPaidText = it },
                placeholder = { Text("0.00", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$currencySymbol ", color = AccentEmerald, fontWeight = FontWeight.Bold) },
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("debt_paid_amount_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Contact Phone (Optional)
            Text(
                text = "Contact Phone / Info (Optional)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                placeholder = { Text("e.g. +1 555-0199", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Days until Due
            Text(
                text = "Due In (Days from today)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = daysUntilDue,
                onValueChange = { daysUntilDue = it },
                placeholder = { Text("14", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text(" days", color = textMutedColor(), fontSize = 12.sp) },
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Notes / Reason
            Text(
                text = "Notes / Purpose",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("e.g. Dinner bill split, concert tickets, rent advance", color = textMutedColor(), fontSize = 13.sp) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = err,
                    color = AccentRose,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Submit / Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (debtToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(debtToEdit)
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentRose.copy(alpha = 0.2f))
                            .border(1.dp, AccentRose.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Debt Record",
                            tint = AccentRose
                        )
                    }
                }

                GlassButton(
                    text = if (debtToEdit == null) "Save Debt Record" else "Update Record",
                    onClick = {
                        val parsedTotal = totalAmountText.toDoubleOrNull()
                        val parsedPaid = amountPaidText.toDoubleOrNull() ?: 0.0
                        val days = daysUntilDue.toLongOrNull() ?: 14L

                        if (personName.isBlank()) {
                            errorMessage = "Please enter a name"
                        } else if (parsedTotal == null || parsedTotal <= 0) {
                            errorMessage = "Please enter a valid total amount"
                        } else {
                            val dueDateMillis = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
                            onSave(
                                personName.trim(),
                                type,
                                parsedTotal,
                                parsedPaid,
                                dueDateMillis,
                                notes.trim(),
                                contactPhone.trim()
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_debt_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
