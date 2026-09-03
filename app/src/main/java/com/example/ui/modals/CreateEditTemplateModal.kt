package com.example.ui.modals

import com.example.ui.theme.*
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.entity.ExpenseTemplateEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.GlassButton
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRose
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditTemplateModal(
    templateToEdit: ExpenseTemplateEntity?,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        category: String,
        amount: Double,
        paymentMethod: String,
        note: String,
        frequency: String,
        isPlanned: Boolean,
        iconName: String,
        colorHex: String
    ) -> Unit,
    onDelete: ((ExpenseTemplateEntity) -> Unit)? = null
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(templateToEdit?.title ?: "") }
    var amountText by remember { mutableStateOf(templateToEdit?.amount?.let { String.format(Locale.US, "%.2f", it) } ?: "") }
    var category by remember { mutableStateOf(templateToEdit?.category ?: "Food & Dining") }
    var paymentMethod by remember { mutableStateOf(templateToEdit?.paymentMethod ?: "Credit Card") }
    var note by remember { mutableStateOf(templateToEdit?.note ?: "") }
    var frequency by remember { mutableStateOf(templateToEdit?.frequency ?: "MONTHLY") }
    var isPlanned by remember { mutableStateOf(templateToEdit?.isPlanned ?: true) }
    var colorHex by remember { mutableStateOf(templateToEdit?.colorHex ?: "#818CF8") }
    var iconName by remember { mutableStateOf(templateToEdit?.iconName ?: "Receipt") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = CategoryRegistry.defaultCategories.map { it.name }
    val paymentMethods = listOf("Credit Card", "Debit Card", "Cash", "Bank Transfer", "Apple Pay / Wallet")
    val frequencies = listOf("DAILY", "WEEKLY", "MONTHLY", "ONE_TIME")
    val templateColors = listOf("#818CF8", "#10B981", "#F59E0B", "#EC4899", "#3B82F6", "#8B5CF6")

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
            // Modal Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (templateToEdit == null) "NEW TEMPLATE" else "EDIT TEMPLATE",
                        color = textMutedColor(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (templateToEdit == null) "Create Expense Preset" else "Update Template",
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

            // Title Field
            Text(
                text = "Template Name",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Weekly Groceries, Gas Top-up", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("template_title_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Field
            Text(
                text = "Estimated Amount ($currencySymbol)",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                placeholder = { Text("0.00", color = textMutedColor(), fontSize = 14.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$currencySymbol ", color = AccentCyan, fontWeight = FontWeight.Bold) },
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("template_amount_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selector
            Text(
                text = "Category",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(4).forEach { catName ->
                    val isSel = catName == category
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) AccentCyan else Color.Transparent)
                            .clickable { category = catName }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = catName.split(" ").first(),
                            color = if (isSel) Color.Black else textSecondaryColor(),
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frequency Chips
            Text(
                text = "Recurrence Frequency",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                frequencies.forEach { freq ->
                    val isSel = freq == frequency
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) AccentCyan.copy(alpha = 0.2f) else glassSurfaceBgColor())
                            .border(1.dp, if (isSel) AccentCyan else glassBorderColor(), RoundedCornerShape(12.dp))
                            .clickable { frequency = freq }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = freq,
                            color = if (isSel) AccentCyan else textSecondaryColor(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Method Selector
            Text(
                text = "Payment Method",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                paymentMethods.take(3).forEach { pm ->
                    val isSel = pm == paymentMethod
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) glassSurfaceBgColor() else glassSurfaceBgColor().copy(alpha = 0.5f))
                            .border(1.dp, if (isSel) AccentCyan else glassBorderColor(), RoundedCornerShape(12.dp))
                            .clickable { paymentMethod = pm }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pm,
                            color = if (isSel) textPrimaryColor() else textMutedColor(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Planned Toggle Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Include in Monthly Plan",
                        color = textPrimaryColor(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Adds this template to your monthly budget projection calculations",
                        color = textMutedColor(),
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = isPlanned,
                    onCheckedChange = { isPlanned = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentCyan,
                        uncheckedThumbColor = textMutedColor(),
                        uncheckedTrackColor = if (isAppInDarkTheme()) Slate800 else Color(0xFFE2E8F0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Note Field
            Text(
                text = "Note / Description",
                color = textSecondaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Optional store, brand, or location details", color = textMutedColor(), fontSize = 13.sp) },
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

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (templateToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(templateToEdit)
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
                            contentDescription = "Delete Template",
                            tint = AccentRose
                        )
                    }
                }

                GlassButton(
                    text = if (templateToEdit == null) "Save Expense Template" else "Update Template",
                    onClick = {
                        val parsedAmount = amountText.toDoubleOrNull()
                        if (title.isBlank()) {
                            errorMessage = "Please enter a template name"
                        } else if (parsedAmount == null || parsedAmount <= 0) {
                            errorMessage = "Please enter a valid amount"
                        } else {
                            onSave(
                                title.trim(),
                                category,
                                parsedAmount,
                                paymentMethod,
                                note.trim(),
                                frequency,
                                isPlanned,
                                iconName,
                                colorHex
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_template_button")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
