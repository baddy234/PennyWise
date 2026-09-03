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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.data.model.FundVisualHelper
import com.example.ui.components.GlassButton
import com.example.ui.theme.*
import java.util.Locale

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material.icons.filled.EventRepeat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionModal(
    transactionToEdit: TransactionEntity? = null,
    funds: List<FundEntity> = emptyList(),
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: String, type: String, note: String, paymentMethod: String, fundId: Long?, fundName: String?, isRecurring: Boolean, recurringInterval: String) -> Unit,
    onDelete: ((TransactionEntity) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(transactionToEdit?.title ?: "") }
    var amountStr by remember { mutableStateOf(transactionToEdit?.let { String.format(Locale.US, "%.2f", it.amount) } ?: "") }
    var transactionType by remember { mutableStateOf(transactionToEdit?.type ?: "EXPENSE") }
    var selectedCategory by remember {
        mutableStateOf(transactionToEdit?.category ?: if (transactionType == "EXPENSE") "Food & Dining" else "Salary & Income")
    }
    var selectedFundId by remember { mutableStateOf<Long?>(transactionToEdit?.fundId) }
    var paymentMethod by remember { mutableStateOf(transactionToEdit?.paymentMethod ?: "Card") }
    var isRecurring by remember { mutableStateOf(transactionToEdit?.isRecurring ?: false) }
    var recurringInterval by remember { mutableStateOf(transactionToEdit?.recurringInterval ?: "MONTHLY") }
    var note by remember { mutableStateOf(transactionToEdit?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isFundDropdownExpanded by remember { mutableStateOf(false) }
    var isPaymentDropdownExpanded by remember { mutableStateOf(false) }

    val categories = CategoryRegistry.defaultCategories.filter {
        if (transactionType == "EXPENSE") !it.isIncome else it.isIncome || it.id == "other"
    }

    val paymentMethods = listOf("Card", "Cash", "Bank Transfer", "Crypto", "Other")
    val selectedFund = funds.find { it.id == selectedFundId }

    val isDark = isAppInDarkTheme()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = glassModalContainerColor(),
        scrimColor = Color.Black.copy(alpha = 0.5f),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isDark) Slate700 else Slate300)
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
                    text = if (transactionToEdit == null) "New Transaction" else "Edit Transaction",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.4).sp
                )

                if (transactionToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(transactionToEdit)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("delete_transaction_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Transaction",
                            tint = AccentRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Type Toggle (Expense / Income)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Slate900 else Slate100)
                    .border(1.dp, if (isDark) Slate800 else Slate300, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                listOf("EXPENSE" to "Expense", "INCOME" to "Income").forEach { (typeKey, label) ->
                    val isSelected = transactionType == typeKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .then(
                                if (isSelected) {
                                    Modifier.background(
                                        if (typeKey == "EXPENSE") Brush.horizontalGradient(listOf(AccentRose.copy(alpha = 0.8f), AccentRose))
                                        else Brush.horizontalGradient(listOf(AccentEmerald.copy(alpha = 0.8f), AccentEmerald))
                                    )
                                } else Modifier
                            )
                            .clickable {
                                transactionType = typeKey
                                selectedCategory = if (typeKey == "EXPENSE") "Food & Dining" else "Salary & Income"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
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
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        color = if (transactionType == "EXPENSE") AccentRose else AccentEmerald,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                placeholder = { Text("0.00", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 20.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = appTextFieldColors(
                    focusedBorderColor = if (transactionType == "EXPENSE") AccentRose else AccentEmerald
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / Merchant", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                placeholder = { Text("e.g., Grocery Mart, Coffee, Rent", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("title_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Dropdown Selector
            Text(
                text = "Category",
                color = textPrimaryColor(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            val currentCategoryObj = CategoryRegistry.getCategory(selectedCategory)

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                        .clickable { isCategoryDropdownExpanded = true }
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(currentCategoryObj.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = currentCategoryObj.icon,
                                contentDescription = null,
                                tint = currentCategoryObj.color,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentCategoryObj.name,
                            color = textPrimaryColor(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Category",
                        tint = textMutedColor()
                    )
                }

                DropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false },
                    modifier = Modifier
                        .background(glassModalContainerColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = cat.color,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = cat.name,
                                        color = textPrimaryColor(),
                                        fontSize = 13.sp,
                                        fontWeight = if (selectedCategory.equals(cat.name, ignoreCase = true)) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            },
                            onClick = {
                                selectedCategory = cat.name
                                isCategoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fund / Account Selector
            if (funds.isNotEmpty()) {
                Text(
                    text = if (transactionType == "EXPENSE") "Deduct From Account / Fund" else "Credit To Account / Fund",
                    color = textSecondaryColor(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(glassSurfaceBgColor())
                            .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                            .clickable { isFundDropdownExpanded = true }
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedFund == null) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = textMutedColor(),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "General Budget (No Specific Fund)",
                                    color = textSecondaryColor(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else {
                                val fundColor = FundVisualHelper.getColor(selectedFund.colorHex)
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(fundColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = FundVisualHelper.getIcon(selectedFund.iconName),
                                        contentDescription = null,
                                        tint = fundColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = selectedFund.name,
                                        color = textPrimaryColor(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Balance: $currencySymbol${String.format(Locale.US, "%.2f", selectedFund.balance)}",
                                        color = textMutedColor(),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Fund",
                            tint = textMutedColor()
                        )
                    }

                    DropdownMenu(
                        expanded = isFundDropdownExpanded,
                        onDismissRequest = { isFundDropdownExpanded = false },
                        modifier = Modifier
                            .background(glassModalContainerColor())
                            .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                    ) {
                        // Option for General Budget
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "General Budget (None)",
                                    color = if (selectedFundId == null) AccentCyan else textSecondaryColor(),
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedFundId == null) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                selectedFundId = null
                                isFundDropdownExpanded = false
                            }
                        )

                        funds.forEach { fund ->
                            val fundColor = FundVisualHelper.getColor(fund.colorHex)
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = FundVisualHelper.getIcon(fund.iconName),
                                            contentDescription = null,
                                            tint = fundColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "${fund.name} ($currencySymbol${String.format(Locale.US, "%.0f", fund.balance)})",
                                            color = if (selectedFundId == fund.id) AccentCyan else textPrimaryColor(),
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedFundId == fund.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    selectedFundId = fund.id
                                    isFundDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Balance impact preview
                if (selectedFund != null) {
                    val amountVal = amountStr.toDoubleOrNull() ?: 0.0
                    val projectedBal = if (transactionType == "EXPENSE") {
                        selectedFund.balance - amountVal
                    } else {
                        selectedFund.balance + amountVal
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (transactionType == "EXPENSE" && projectedBal < 0) AccentRose.copy(alpha = 0.15f)
                                else if (isDark) Slate900.copy(alpha = 0.8f) else Color(0xFFF1F5F9)
                            )
                            .border(
                                1.dp,
                                if (transactionType == "EXPENSE" && projectedBal < 0) AccentRose.copy(alpha = 0.4f)
                                else if (isDark) Slate800 else Color(0xFFE2E8F0),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (transactionType == "EXPENSE" && projectedBal < 0) "⚠️ Exceeds balance" else "${selectedFund.name} projected balance:",
                                color = if (transactionType == "EXPENSE" && projectedBal < 0) AccentRose else textMutedColor(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$currencySymbol${String.format(Locale.US, "%.2f", selectedFund.balance)} → $currencySymbol${String.format(Locale.US, "%.2f", projectedBal)}",
                                color = if (projectedBal < 0) AccentRose else AccentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Payment Method Selector
            Text(
                text = "Payment Method",
                color = textSecondaryColor(),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                        .clickable { isPaymentDropdownExpanded = true }
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = AccentCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = paymentMethod,
                            color = textPrimaryColor(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Payment Method",
                        tint = textMutedColor()
                    )
                }

                DropdownMenu(
                    expanded = isPaymentDropdownExpanded,
                    onDismissRequest = { isPaymentDropdownExpanded = false },
                    modifier = Modifier
                        .background(glassModalContainerColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = method,
                                    color = if (paymentMethod == method) AccentCyan else textPrimaryColor(),
                                    fontSize = 13.sp,
                                    fontWeight = if (paymentMethod == method) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                paymentMethod = method
                                isPaymentDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recurring / Subscription Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(glassSurfaceBgColor())
                    .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Recurring Subscription",
                                    color = textPrimaryColor(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Track as fixed monthly/weekly bill",
                                    color = textMutedColor(),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Switch(
                            checked = isRecurring,
                            onCheckedChange = { isRecurring = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentCyan,
                                uncheckedThumbColor = textMutedColor(),
                                uncheckedTrackColor = if (isDark) Slate800 else Color(0xFFE2E8F0)
                            )
                        )
                    }

                    if (isRecurring) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("WEEKLY", "MONTHLY", "YEARLY").forEach { interval ->
                                val isSelected = recurringInterval == interval
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) AccentCyan.copy(alpha = 0.2f) else glassSurfaceBgColor())
                                        .border(
                                            1.dp,
                                            if (isSelected) AccentCyan else glassBorderColor(),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { recurringInterval = interval }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = interval.lowercase().replaceFirstChar { it.uppercase() },
                                        color = if (isSelected) AccentCyan else textMutedColor(),
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Note (Optional)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)", color = textMutedColor()) },
                placeholder = { Text("Add receipts info or memos...", color = textMutedColor().copy(alpha = 0.7f)) },
                maxLines = 2,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

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
                text = if (transactionToEdit == null) "Record Transaction" else "Save Changes",
                onClick = {
                    val amountVal = amountStr.toDoubleOrNull()
                    if (amountVal == null || amountVal <= 0) {
                        errorMessage = "Please enter a valid amount greater than zero."
                        return@GlassButton
                    }
                    if (title.isBlank()) {
                        errorMessage = "Please enter a transaction title."
                        return@GlassButton
                    }
                    errorMessage = null
                    onSave(
                        title.trim(),
                        amountVal,
                        selectedCategory,
                        transactionType,
                        note.trim(),
                        paymentMethod,
                        selectedFundId,
                        selectedFund?.name,
                        isRecurring,
                        recurringInterval
                    )
                    onDismiss()
                },
                gradient = if (transactionType == "EXPENSE") {
                    Brush.horizontalGradient(listOf(AccentRose, Color(0xFFE11D48)))
                } else {
                    Brush.horizontalGradient(listOf(AccentEmerald, Color(0xFF059669)))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_transaction_button")
            )
        }
    }
}
