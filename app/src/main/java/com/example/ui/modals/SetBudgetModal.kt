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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.model.CategoryRegistry
import com.example.ui.components.GlassButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SetBudgetModal(
    limitToEdit: BudgetLimitEntity? = null,
    initialPeriodType: String = "MONTHLY",
    initialCategory: String? = null,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onSave: (id: Long, periodType: String, categoryName: String?, amount: Double, isEnabled: Boolean, threshold: Int) -> Unit,
    onDelete: ((BudgetLimitEntity) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var periodType by remember { mutableStateOf(limitToEdit?.periodType ?: initialPeriodType) }
    var categoryName by remember { mutableStateOf(limitToEdit?.categoryName ?: initialCategory ?: "Food & Dining") }
    var limitAmountStr by remember {
        mutableStateOf(limitToEdit?.let { String.format(java.util.Locale.US, "%.2f", it.limitAmount) } ?: "")
    }
    var isEnabled by remember { mutableStateOf(limitToEdit?.isEnabled ?: true) }
    var notifyThreshold by remember { mutableIntStateOf(limitToEdit?.notifyThresholdPercent ?: 80) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val periods = listOf(
        "DAILY" to "Daily",
        "WEEKLY" to "Weekly",
        "MONTHLY" to "Monthly",
        "YEARLY" to "Yearly",
        "CATEGORY" to "Category"
    )

    val expenseCategories = CategoryRegistry.defaultCategories.filter { !it.isIncome }
    val thresholds = listOf(50, 75, 80, 90, 100)

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
                .padding(bottom = 28.dp)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (limitToEdit == null) "Set Budget Limit" else "Edit Budget Limit",
                    color = textPrimaryColor(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )

                if (limitToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(limitToEdit)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("delete_limit_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Limit",
                            tint = AccentRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Period Selector
            Text(
                text = "Budget Target Period",
                color = textSecondaryColor(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(10.dp))
                    .padding(2.dp)
            ) {
                periods.forEach { (key, label) ->
                    val isSelected = periodType == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.background(Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)))
                                } else Modifier
                            )
                            .clickable { periodType = key },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else textMutedColor(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Category Picker if periodType == "CATEGORY"
            if (periodType == "CATEGORY") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select Category",
                    color = textSecondaryColor(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    expenseCategories.forEach { cat ->
                        val isSelected = categoryName.equals(cat.name, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) cat.color.copy(alpha = 0.25f) else glassSurfaceBgColor())
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) cat.color else glassBorderColor(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { categoryName = cat.name }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = cat.icon,
                                contentDescription = cat.name,
                                tint = if (isSelected) cat.color else textMutedColor(),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = cat.name,
                                color = if (isSelected) textPrimaryColor() else textSecondaryColor(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Limit Amount Input
            OutlinedTextField(
                value = limitAmountStr,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        limitAmountStr = it
                    }
                },
                label = { Text("Limit Cap Amount", color = textMutedColor()) },
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        color = AccentCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                placeholder = { Text("e.g., 500.00", color = textMutedColor().copy(alpha = 0.7f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_limit_input")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Notification Threshold
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = "Alert Threshold",
                        tint = AccentCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notify when spend reaches:",
                        color = textSecondaryColor(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                thresholds.forEach { pct ->
                    val isSelected = notifyThreshold == pct
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentCyan.copy(alpha = 0.25f) else glassSurfaceBgColor())
                            .border(
                                1.dp,
                                if (isSelected) AccentCyan else glassBorderColor(),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { notifyThreshold = pct }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$pct%",
                            color = if (isSelected) AccentCyan else textMutedColor(),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enable switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Enable Limit Monitoring",
                        color = textPrimaryColor(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Send push alerts when exceeding limit",
                        color = textMutedColor(),
                        fontSize = 11.sp
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { isEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentCyan,
                        uncheckedThumbColor = textMutedColor(),
                        uncheckedTrackColor = if (isAppInDarkTheme()) Slate800 else Color(0xFFE2E8F0)
                    )
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = AccentRose,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Action Button
            GlassButton(
                text = "Save Budget Ceiling",
                onClick = {
                    val amountVal = limitAmountStr.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        errorMessage = "Please enter a valid budget amount greater than 0."
                        return@GlassButton
                    }
                    errorMessage = null
                    val cat = if (periodType == "CATEGORY") categoryName else null
                    onSave(limitToEdit?.id ?: 0, periodType, cat, amountVal, isEnabled, notifyThreshold)
                    onDismiss()
                },
                gradient = Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_limit_button")
            )
        }
    }
}
