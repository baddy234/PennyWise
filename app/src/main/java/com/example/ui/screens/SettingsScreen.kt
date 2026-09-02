package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BadgeVariant
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.ShadcnBadge
import com.example.ui.theme.*

import com.example.ui.modals.SecuritySettingsModal

import androidx.compose.material.icons.filled.Palette
import com.example.ui.theme.AppThemeMode

data class CurrencyOption(
    val symbol: String,
    val code: String,
    val name: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currencySymbol: String,
    notificationsEnabled: Boolean,
    isPinLockEnabled: Boolean = false,
    currentPin: String = "1234",
    transactionCount: Int,
    limitCount: Int,
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    onThemeModeChange: (AppThemeMode) -> Unit = {},
    onCurrencyChange: (String) -> Unit,
    onNotificationToggle: (Boolean) -> Unit,
    onSavePinSettings: (Boolean, String) -> Unit = { _, _ -> },
    onTestNotification: () -> Unit,
    onExportCsv: () -> Unit,
    onExportJson: (Context) -> Unit,
    onRestoreJson: (String) -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Modal Visibility States
    var showThemeModal by remember { mutableStateOf(false) }
    var showCurrencyModal by remember { mutableStateOf(false) }
    var showNotificationsModal by remember { mutableStateOf(false) }
    var showSecurityModal by remember { mutableStateOf(false) }
    var showBackupModal by remember { mutableStateOf(false) }
    var showDangerZoneModal by remember { mutableStateOf(false) }
    var showAboutModal by remember { mutableStateOf(false) }

    val currencyList = listOf(
        CurrencyOption("$", "USD", "US Dollar"),
        CurrencyOption("GH₵", "GHS", "Ghanaian Cedi"),
        CurrencyOption("₵", "GHS", "Ghana Cedi (₵)"),
        CurrencyOption("€", "EUR", "Euro"),
        CurrencyOption("£", "GBP", "British Pound"),
        CurrencyOption("¥", "JPY", "Japanese Yen"),
        CurrencyOption("₹", "INR", "Indian Rupee"),
        CurrencyOption("C$", "CAD", "Canadian Dollar"),
        CurrencyOption("A$", "AUD", "Australian Dollar"),
        CurrencyOption("CHF", "CHF", "Swiss Franc"),
        CurrencyOption("₦", "NGN", "Nigerian Naira"),
        CurrencyOption("฿", "THB", "Thai Baht"),
        CurrencyOption("₩", "KRW", "South Korean Won"),
        CurrencyOption("R$", "BRL", "Brazilian Real"),
        CurrencyOption("kr", "SEK", "Swedish Krona"),
        CurrencyOption("AED", "AED", "UAE Dirham"),
        CurrencyOption("S$", "SGD", "Singapore Dollar"),
        CurrencyOption("Mex$", "MXN", "Mexican Peso")
    )

    val currentCurrencyName = currencyList.find { it.symbol == currencySymbol }?.name ?: "Custom"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Preferences, notifications, data & security",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                ShadcnBadge(
                    text = "Offline Mode",
                    variant = BadgeVariant.SUCCESS
                )
            }
        }

        // Section: General Preferences
        item {
            Text(
                text = "PREFERENCES",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Theme & Appearance Setting Row
                    SettingItemRow(
                        icon = Icons.Default.Palette,
                        iconTint = AccentViolet,
                        iconBg = AccentViolet.copy(alpha = 0.15f),
                        title = "App Theme & Appearance",
                        subtitle = when (themeMode) {
                            AppThemeMode.SYSTEM -> "System Default • Follows Android device theme"
                            AppThemeMode.DARK -> "Dark Glass • Obsidian canvas & glowing accents"
                            AppThemeMode.LIGHT -> "Light Glass • Frosted slate canvas & crisp text"
                        },
                        trailingBadge = themeMode.displayName,
                        badgeVariant = BadgeVariant.CYAN,
                        onClick = { showThemeModal = true },
                        testTag = "setting_theme_row"
                    )

                    SettingDivider()

                    // Currency Selection Setting Row
                    SettingItemRow(
                        icon = Icons.Default.AttachMoney,
                        iconTint = AccentCyan,
                        iconBg = AccentCyan.copy(alpha = 0.15f),
                        title = "Display Currency",
                        subtitle = "$currentCurrencyName ($currencySymbol)",
                        trailingBadge = currencySymbol,
                        onClick = { showCurrencyModal = true },
                        testTag = "setting_currency_row"
                    )

                    SettingDivider()

                    // Notifications Setting Row
                    SettingItemRow(
                        icon = Icons.Default.NotificationsActive,
                        iconTint = Color(0xFFFBBF24),
                        iconBg = Color(0x28F59E0B),
                        title = "Budget Alerts & Notifications",
                        subtitle = if (notificationsEnabled) "Active • Push alerts on limit breach" else "Muted • Alerts disabled",
                        trailingBadge = if (notificationsEnabled) "Enabled" else "Off",
                        badgeVariant = if (notificationsEnabled) BadgeVariant.CYAN else BadgeVariant.SECONDARY,
                        onClick = { showNotificationsModal = true },
                        testTag = "setting_notifications_row"
                    )

                    SettingDivider()

                    // App Lock & Security Row
                    SettingItemRow(
                        icon = Icons.Default.Security,
                        iconTint = AccentCyan,
                        iconBg = AccentCyan.copy(alpha = 0.15f),
                        title = "App Lock & Security PIN",
                        subtitle = if (isPinLockEnabled) "Enabled • Require 4-digit PIN on launch" else "Disabled • Tap to setup PIN lock",
                        trailingBadge = if (isPinLockEnabled) "Locked" else "Off",
                        badgeVariant = if (isPinLockEnabled) BadgeVariant.CYAN else BadgeVariant.SECONDARY,
                        onClick = { showSecurityModal = true },
                        testTag = "setting_security_row"
                    )
                }
            }
        }

        // Section: Data & Portability
        item {
            Text(
                text = "DATA & STORAGE",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Backup & Export Setting Row
                    SettingItemRow(
                        icon = Icons.Default.Storage,
                        iconTint = AccentEmerald,
                        iconBg = AccentEmerald.copy(alpha = 0.15f),
                        title = "Backup, Export & Restore",
                        subtitle = "$transactionCount transactions • $limitCount budget limits",
                        trailingBadge = "Encrypted",
                        badgeVariant = BadgeVariant.SUCCESS,
                        onClick = { showBackupModal = true },
                        testTag = "setting_backup_row"
                    )

                    SettingDivider()

                    // Danger Zone / Clear Data Setting Row
                    SettingItemRow(
                        icon = Icons.Default.DeleteForever,
                        iconTint = AccentRose,
                        iconBg = AccentRose.copy(alpha = 0.15f),
                        title = "Reset & Clear Data",
                        subtitle = "Wipe transaction logs or restore initial state",
                        trailingBadge = "Danger",
                        badgeVariant = BadgeVariant.DESTRUCTIVE,
                        onClick = { showDangerZoneModal = true },
                        testTag = "setting_danger_row"
                    )
                }
            }
        }

        // Section: About & Privacy
        item {
            Text(
                text = "APPLICATION",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    SettingItemRow(
                        icon = Icons.Default.Info,
                        iconTint = AccentIndigo,
                        iconBg = AccentIndigo.copy(alpha = 0.15f),
                        title = "About PennyWise",
                        subtitle = "By RIEL SITES • v1.0 • 100% on-device SQLite storage",
                        trailingBadge = "RIEL SITES",
                        badgeVariant = BadgeVariant.CYAN,
                        onClick = { showAboutModal = true },
                        testTag = "setting_about_row"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // ==========================================
    // MODAL 0: THEME SELECTION MODAL
    // ==========================================
    if (showThemeModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val isDark = isAppInDarkTheme()
        ModalBottomSheet(
            onDismissRequest = { showThemeModal = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            scrimColor = Color.Black.copy(alpha = 0.5f),
            dragHandle = { ModalDragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Glassmorphic Theme Mode",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Select system-wide visual appearance",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    ShadcnBadge(
                        text = themeMode.displayName,
                        variant = BadgeVariant.CYAN
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                val themeOptions = listOf(
                    Triple(AppThemeMode.LIGHT, "Light Glass", "Clean frosted slate canvas with high contrast dark text"),
                    Triple(AppThemeMode.DARK, "Dark Glass", "Sleek obsidian canvas with glowing neon ambient accents"),
                    Triple(AppThemeMode.SYSTEM, "System Default", "Automatically match your Android device light/dark mode")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    themeOptions.forEach { (mode, title, desc) ->
                        val isSelected = themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) AccentViolet.copy(alpha = if (isDark) 0.18f else 0.12f) else if (isDark) Slate900 else Slate100)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentViolet.copy(alpha = 0.8f) else if (isDark) Slate800 else Slate300,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    onThemeModeChange(mode)
                                    showThemeModal = false
                                }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AccentViolet),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL 1: CURRENCY SELECTION MODAL
    // ==========================================
    if (showCurrencyModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showCurrencyModal = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = { ModalDragHandle() }
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Display Currency",
                            color = Slate50,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Select your preferred currency denomination",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }

                    ShadcnBadge(
                        text = "Current: $currencySymbol",
                        variant = BadgeVariant.CYAN
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Currency Grid List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currencyList.forEach { option ->
                        val isSelected = option.symbol == currencySymbol
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentCyan.copy(alpha = 0.18f) else Slate900)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentCyan.copy(alpha = 0.8f) else Slate800,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onCurrencyChange(option.symbol)
                                    showCurrencyModal = false
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) AccentCyan else Slate800),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = option.symbol,
                                        color = if (isSelected) Color.Black else Slate200,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = option.name,
                                        color = if (isSelected) Slate50 else Slate200,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Text(
                                        text = option.code,
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(AccentCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL 2: NOTIFICATIONS & ALERTS MODAL
    // ==========================================
    if (showNotificationsModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showNotificationsModal = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = { ModalDragHandle() }
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
                            .background(Color(0x28F59E0B))
                            .border(1.dp, Color(0x60F59E0B), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Budget Alerts & Notifications",
                            color = Slate50,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Configure proactive threshold warnings",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Master Toggle Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Push Notifications",
                                color = Slate50,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Receive on-device alert banners when spending nears or breaches your ceilings.",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onNotificationToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentCyan,
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = Slate800
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert Thresholds Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900.copy(alpha = 0.6f))
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Alert Threshold Levels",
                            color = Slate300,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFBBF24)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "80% Warning: Triggered when period budget exceeds 80%",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentRose))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "100% Critical Breach: Triggered when budget is exhausted",
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Test Notification Action Button
                GlassButton(
                    text = "Send Test Alert Notification",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = {
                        onTestNotification()
                    },
                    gradient = Brush.horizontalGradient(listOf(Color(0xFFD97706), Color(0xFFF59E0B))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_notification_button")
                )
            }
        }
    }

    // ==========================================
    // MODAL: SECURITY SETTINGS MODAL
    // ==========================================
    if (showSecurityModal) {
        SecuritySettingsModal(
            isPinEnabled = isPinLockEnabled,
            currentPin = currentPin,
            onDismiss = { showSecurityModal = false },
            onSavePinSettings = { enabled, newPin ->
                onSavePinSettings(enabled, newPin)
            }
        )
    }

    // ==========================================
    // MODAL 3: BACKUP, EXPORT & RESTORE MODAL
    // ==========================================
    if (showBackupModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var isRestoreExpanded by remember { mutableStateOf(false) }
        var restoreJsonInput by remember { mutableStateOf("") }
        var restoreError by remember { mutableStateOf<String?>(null) }

        ModalBottomSheet(
            onDismissRequest = { showBackupModal = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = { ModalDragHandle() }
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
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentEmerald.copy(alpha = 0.15f))
                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = AccentEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Backup & Data Portability",
                            color = Slate50,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Export financial records or restore past snapshot",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$transactionCount", color = Slate50, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Transactions", color = Slate400, fontSize = 11.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Slate800))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$limitCount", color = Slate50, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Ceiling Limits", color = Slate400, fontSize = 11.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Slate800))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Room SQLite", color = AccentEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Encrypted", color = Slate400, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Export Options
                Text(
                    text = "EXPORT OPTIONS",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // CSV Export Button
                GlassButton(
                    text = "Export Transactions as CSV",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = {
                        showBackupModal = false
                        onExportCsv()
                    },
                    gradient = Brush.horizontalGradient(listOf(AccentEmerald, Color(0xFF0D9488))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_csv_button")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Full JSON Export Button
                GlassButton(
                    text = "Export Full Database (JSON Backup)",
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    onClick = {
                        onExportJson(context)
                    },
                    gradient = Brush.horizontalGradient(listOf(AccentIndigo, AccentViolet)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_json_button")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Restore Section
                Text(
                    text = "RESTORE DATABASE",
                    color = Slate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (!isRestoreExpanded) {
                    GlassButton(
                        text = "Paste & Restore JSON Backup",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                tint = Slate200,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onClick = { isRestoreExpanded = true },
                        isSecondary = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("restore_json_button")
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Slate900)
                            .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Paste JSON Backup Content:",
                            color = Slate200,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = restoreJsonInput,
                            onValueChange = {
                                restoreJsonInput = it
                                restoreError = null
                            },
                            placeholder = { Text("{\"version\": 1, \"transactions\": [...]}", color = Slate700) },
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate200,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = Slate700,
                                focusedContainerColor = Slate950,
                                unfocusedContainerColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (restoreError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = restoreError!!, color = AccentRose, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            GlassButton(
                                text = "Cancel",
                                onClick = {
                                    isRestoreExpanded = false
                                    restoreJsonInput = ""
                                    restoreError = null
                                },
                                isSecondary = true,
                                modifier = Modifier.weight(1f)
                            )
                            GlassButton(
                                text = "Apply Restore",
                                onClick = {
                                    if (restoreJsonInput.isBlank()) {
                                        restoreError = "Please paste valid backup JSON."
                                        return@GlassButton
                                    }
                                    onRestoreJson(restoreJsonInput.trim())
                                    showBackupModal = false
                                },
                                gradient = Brush.horizontalGradient(listOf(AccentCyan, AccentIndigo)),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL 4: RESET & DANGER ZONE MODAL
    // ==========================================
    if (showDangerZoneModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showDangerZoneModal = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = { ModalDragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // Warning Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AccentRose.copy(alpha = 0.2f))
                            .border(1.dp, AccentRose.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = AccentRose,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Reset & Clear Data",
                            color = Slate50,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "Irreversible action on local database",
                            color = AccentRose,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x284C0519))
                        .border(1.dp, AccentRose.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "What happens when you wipe data:",
                            color = Slate100,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• All $transactionCount recorded expense and income entries will be deleted.",
                            color = Color(0xFFFFE4E6),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• Your custom budget limits and funds setup will remain preserved.",
                            color = Color(0xFFFFE4E6),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "• To prevent accidental data loss, consider exporting a JSON backup first.",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GlassButton(
                        text = "Cancel",
                        onClick = { showDangerZoneModal = false },
                        isSecondary = true,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "Wipe All Transactions",
                        onClick = {
                            onClearAllData()
                            showDangerZoneModal = false
                        },
                        gradient = Brush.horizontalGradient(listOf(AccentRose, Color(0xFFBE123C))),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("clear_data_button")
                    )
                }
            }
        }
    }

    // ==========================================
    // MODAL 5: ABOUT & PRIVACY ASSURANCE MODAL
    // ==========================================
    if (showAboutModal) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAboutModal = false },
            sheetState = sheetState,
            containerColor = DarkSurface,
            scrimColor = Color.Black.copy(alpha = 0.7f),
            dragHandle = { ModalDragHandle() }
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
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0x4406B6D4),
                                        Color(0x220284C7),
                                        Color(0x550F172A)
                                    )
                                )
                            )
                            .border(1.dp, AccentCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_pennywise_logo),
                            contentDescription = "PennyWise Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PennyWise",
                            color = Slate50,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp
                        )
                        Text(
                            text = "A Product by RIEL SITES • v1.0",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Slate900)
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "100% On-Device Room Database", color = Slate100, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = "Your financial figures, transactions, notes, and limits never leave your device. No third-party analytical trackers or cloud data transmission.",
                            color = Slate400,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Modern Architecture", color = Slate100, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = "Built with Android Jetpack Compose, Material 3, Kotlin Coroutines & Flows, and shadcn-inspired glassmorphism.",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassButton(
                    text = "Close",
                    onClick = { showAboutModal = false },
                    isSecondary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SettingItemRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    trailingBadge: String? = null,
    badgeVariant: BadgeVariant = BadgeVariant.DEFAULT,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingBadge != null) {
                ShadcnBadge(
                    text = trailingBadge,
                    variant = badgeVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun SettingDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(if (isAppInDarkTheme()) Slate800.copy(alpha = 0.6f) else Slate200)
    )
}

@Composable
private fun ModalDragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(40.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(if (isAppInDarkTheme()) Slate700 else Slate300)
    )
}
