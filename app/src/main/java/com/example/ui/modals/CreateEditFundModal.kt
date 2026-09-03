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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditFundModal(
    fundToEdit: FundEntity? = null,
    currencySymbol: String = "$",
    onDismiss: () -> Unit,
    onSave: (name: String, balance: Double, targetAmount: Double?, colorHex: String, iconName: String, note: String) -> Unit,
    onDelete: ((FundEntity) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(fundToEdit?.name ?: "") }
    var balanceStr by remember {
        mutableStateOf(fundToEdit?.let { String.format(Locale.US, "%.2f", it.balance) } ?: "0.00")
    }
    var targetStr by remember {
        mutableStateOf(fundToEdit?.targetAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var selectedColorHex by remember { mutableStateOf(fundToEdit?.colorHex ?: "#6366F1") }
    var selectedIconName by remember { mutableStateOf(fundToEdit?.iconName ?: "Wallet") }
    var note by remember { mutableStateOf(fundToEdit?.note ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Sub-modal states for decluttered UI
    var showPresetDropdown by remember { mutableStateOf(false) }
    var showIconPickerModal by remember { mutableStateOf(false) }
    var showColorPickerModal by remember { mutableStateOf(false) }

    val presetNames = listOf("Main Bank", "Emergency Fund", "Savings Vault", "Cash Wallet", "Vacation Stash", "Investments")
    val activeColor = FundVisualHelper.getColor(selectedColorHex)

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val fundColor = FundVisualHelper.getColor(selectedColorHex)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(fundColor.copy(alpha = 0.2f))
                            .border(1.dp, fundColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = FundVisualHelper.getIcon(selectedIconName),
                            contentDescription = null,
                            tint = fundColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (fundToEdit == null) "Create New Fund" else "Edit Fund",
                            color = textPrimaryColor(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Manage money buckets & targets",
                            color = textMutedColor(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                if (fundToEdit != null && onDelete != null) {
                    IconButton(
                        onClick = {
                            onDelete(fundToEdit)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("delete_fund_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Fund",
                            tint = AccentRose
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Preset Dropdown Trigger (Only when creating new fund)
            if (fundToEdit == null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(glassSurfaceBgColor())
                            .border(1.dp, AccentCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable { showPresetDropdown = true }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Autofill from Quick Preset",
                                color = textSecondaryColor(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Preset",
                            tint = textMutedColor()
                        )
                    }

                    DropdownMenu(
                        expanded = showPresetDropdown,
                        onDismissRequest = { showPresetDropdown = false },
                        modifier = Modifier
                            .background(glassModalContainerColor())
                            .border(1.dp, glassBorderColor(), RoundedCornerShape(12.dp))
                    ) {
                        presetNames.forEach { preset ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = preset,
                                        color = textPrimaryColor(),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    name = preset
                                    when (preset) {
                                        "Main Bank" -> { selectedIconName = "Bank"; selectedColorHex = "#38BDF8" }
                                        "Emergency Fund" -> { selectedIconName = "Emergency"; selectedColorHex = "#34D399" }
                                        "Savings Vault" -> { selectedIconName = "Savings"; selectedColorHex = "#A855F7" }
                                        "Cash Wallet" -> { selectedIconName = "Cash"; selectedColorHex = "#FBBF24" }
                                        "Vacation Stash" -> { selectedIconName = "Travel"; selectedColorHex = "#FB7185" }
                                        "Investments" -> { selectedIconName = "Investment"; selectedColorHex = "#14B8A6" }
                                    }
                                    showPresetDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Fund Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Fund / Account Name", color = textMutedColor()) },
                placeholder = { Text("e.g. Main Bank, Travel Stash", color = textMutedColor().copy(alpha = 0.7f)) },
                singleLine = true,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fund_name_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Current Balance & Target Goal Inputs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^-?\\d*\\.?\\d{0,2}$"))) {
                            balanceStr = it
                        }
                    },
                    label = { Text("Current Balance", color = textMutedColor()) },
                    leadingIcon = {
                        Text(
                            text = currencySymbol,
                            color = AccentEmerald,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = appTextFieldColors(focusedBorderColor = AccentEmerald),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fund_balance_input")
                )

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            targetStr = it
                        }
                    },
                    label = { Text("Target Goal (Opt.)", color = textMutedColor()) },
                    placeholder = { Text("None", color = textMutedColor().copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Text(
                            text = currencySymbol,
                            color = AccentCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fund_target_input")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Decluttered Customization Row (Icon Modal Trigger & Color Modal Trigger)
            Text(
                text = "Appearance & Customization",
                color = textMutedColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon Selector Button -> opens Icon Picker Modal
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                        .clickable { showIconPickerModal = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = FundVisualHelper.getIcon(selectedIconName),
                            contentDescription = null,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = selectedIconName,
                            color = textPrimaryColor(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Change Icon",
                        tint = textMutedColor()
                    )
                }

                // Color Selector Button -> opens Color Picker Modal
                val colorOption = FundVisualHelper.colorOptions.find { it.hex.equals(selectedColorHex, ignoreCase = true) }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(glassSurfaceBgColor())
                        .border(1.dp, glassBorderColor(), RoundedCornerShape(14.dp))
                        .clickable { showColorPickerModal = true }
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(activeColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = colorOption?.label ?: "Theme",
                            color = textPrimaryColor(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Change Color",
                        tint = textMutedColor(),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note / Purpose Input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note / Purpose (Optional)", color = textMutedColor()) },
                placeholder = { Text("e.g. Account number, rainy day goal", color = textMutedColor().copy(alpha = 0.7f)) },
                maxLines = 2,
                colors = appTextFieldColors(focusedBorderColor = AccentCyan),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage!!,
                    color = AccentRose,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            GlassButton(
                text = if (fundToEdit == null) "Create Fund" else "Save Fund Changes",
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Please enter a fund name."
                        return@GlassButton
                    }
                    val bal = balanceStr.toDoubleOrNull() ?: 0.0
                    val target = targetStr.toDoubleOrNull()
                    errorMessage = null
                    onSave(name.trim(), bal, target, selectedColorHex, selectedIconName, note.trim())
                    onDismiss()
                },
                gradient = Brush.horizontalGradient(listOf(activeColor, AccentIndigo)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_fund_button")
            )
        }
    }

    // --- Sub-Modal 1: Icon Picker Modal ---
    if (showIconPickerModal) {
        AlertDialog(
            onDismissRequest = { showIconPickerModal = false },
            containerColor = glassModalContainerColor(),
            title = {
                Text(
                    text = "Select Fund Icon",
                    color = textPrimaryColor(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(FundVisualHelper.iconOptions) { opt ->
                        val isSelected = selectedIconName.equals(opt.name, ignoreCase = true)
                        val activeColor = FundVisualHelper.getColor(selectedColorHex)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) activeColor.copy(alpha = 0.25f) else glassSurfaceBgColor())
                                .border(
                                    1.dp,
                                    if (isSelected) activeColor else glassBorderColor(),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedIconName = opt.name
                                    showIconPickerModal = false
                                }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = opt.icon,
                                contentDescription = opt.label,
                                tint = if (isSelected) activeColor else textMutedColor(),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = opt.label,
                                color = if (isSelected) textPrimaryColor() else textSecondaryColor(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showIconPickerModal = false }) {
                    Text("Close", color = AccentCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // --- Sub-Modal 2: Color Theme Picker Modal ---
    if (showColorPickerModal) {
        AlertDialog(
            onDismissRequest = { showColorPickerModal = false },
            containerColor = glassModalContainerColor(),
            title = {
                Text(
                    text = "Select Color Theme",
                    color = textPrimaryColor(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(240.dp)
                ) {
                    items(FundVisualHelper.colorOptions) { opt ->
                        val isSelected = selectedColorHex.equals(opt.hex, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) opt.color.copy(alpha = 0.25f) else glassSurfaceBgColor())
                                .border(
                                    1.dp,
                                    if (isSelected) opt.color else glassBorderColor(),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedColorHex = opt.hex
                                    showColorPickerModal = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(opt.color),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = opt.label,
                                color = if (isSelected) textPrimaryColor() else textSecondaryColor(),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPickerModal = false }) {
                    Text("Close", color = AccentCyan, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
