package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.MoreHoriz
import com.example.ui.modals.MoreNavigationModal
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import com.example.ui.components.AmbientGlassBackground
import com.example.ui.modals.AddEditTransactionModal
import com.example.ui.modals.CreateEditFundModal
import com.example.ui.modals.ExportCsvModal
import com.example.ui.modals.PinLockOverlay
import com.example.ui.modals.SecuritySettingsModal
import com.example.ui.modals.SetBudgetModal
import com.example.ui.modals.SubscriptionsModal
import com.example.ui.modals.TopUpFundModal
import com.example.ui.screens.BudgetLimitsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FundsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.BudgetViewModel
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.ReceiptLong
import com.example.data.local.entity.ExpenseTemplateEntity
import com.example.ui.modals.CreateEditTemplateModal
import com.example.ui.screens.ExpensesScreen
import com.example.ui.screens.ToolsScreen

enum class AppNavigationTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Tracker", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_dashboard"),
    EXPENSES("Expenses", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_expenses"),
    FUNDS("Funds", Icons.Filled.Savings, Icons.Outlined.Savings, "nav_funds"),
    TOOLS("Tools", Icons.Filled.Calculate, Icons.Outlined.Calculate, "nav_tools"),
    REPORTS("Reports", Icons.Filled.Insights, Icons.Outlined.Insights, "nav_reports"),
    LIMITS("Ceilings", Icons.Filled.Tune, Icons.Outlined.Tune, "nav_limits"),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BudgetViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            MyApplicationTheme(themeMode = themeMode) {
                BudgetApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BudgetApp(
    viewModel: BudgetViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Request Notification permission on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Observe State
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val limits by viewModel.budgetLimits.collectAsStateWithLifecycle()
    val funds by viewModel.funds.collectAsStateWithLifecycle()
    val expenseTemplates by viewModel.expenseTemplates.collectAsStateWithLifecycle()
    val budgetForecast by viewModel.budgetForecast.collectAsStateWithLifecycle()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val isPinLockEnabled by viewModel.isPinLockEnabled.collectAsStateWithLifecycle()
    val userPin by viewModel.userPin.collectAsStateWithLifecycle()
    val isAppUnlocked by viewModel.isAppUnlocked.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearStatusMessage()
            }
        }
    }

    var currentTab by remember { mutableStateOf(AppNavigationTab.DASHBOARD) }

    // Modals State
    var showAddTransactionModal by remember { mutableStateOf(false) }
    var selectedTransactionToEdit by remember { mutableStateOf<TransactionEntity?>(null) }

    var showCreateEditTemplateModal by remember { mutableStateOf(false) }
    var selectedTemplateToEdit by remember { mutableStateOf<ExpenseTemplateEntity?>(null) }

    var showSetBudgetModal by remember { mutableStateOf(false) }
    var selectedLimitToEdit by remember { mutableStateOf<BudgetLimitEntity?>(null) }

    var showCreateEditFundModal by remember { mutableStateOf(false) }
    var selectedFundToEdit by remember { mutableStateOf<FundEntity?>(null) }

    var showTopUpFundModal by remember { mutableStateOf(false) }
    var selectedFundToTopUp by remember { mutableStateOf<FundEntity?>(null) }

    var showExportCsvModal by remember { mutableStateOf(false) }
    var showSubscriptionsModal by remember { mutableStateOf(false) }
    var showMoreNavModal by remember { mutableStateOf(false) }

    var isAppLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1800)
        isAppLoading = false
    }

    val periodSummary = viewModel.getPeriodSummary(transactions, limits, selectedTimeframe)
    val categoryBreakdown = viewModel.getCategorySpendBreakdown(transactions, selectedTimeframe)
    val barChartData = viewModel.getBarChartData(transactions, selectedTimeframe)
    val insights = viewModel.generateInsights(transactions, limits, selectedTimeframe)

    if (isAppLoading) {
        SplashScreen()
    } else if (isPinLockEnabled && !isAppUnlocked) {
        PinLockOverlay(
            correctPin = userPin,
            onUnlockSuccess = { viewModel.unlockApp() }
        )
    } else {
        AmbientGlassBackground {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                // Glassmorphic Navigation Bar
                val isDark = isAppInDarkTheme()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (isDark) Slate950Alpha() else Color.White.copy(alpha = 0.92f))
                        .border(
                            1.dp,
                            if (isDark) Brush.linearGradient(listOf(Color(0x35FFFFFF), Color(0x10FFFFFF), Color(0x25818CF8)))
                            else Brush.linearGradient(listOf(Color(0x35000000), Color(0x15000000))),
                            RoundedCornerShape(22.dp)
                        )
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(64.dp)
                    ) {
                        // Core 3 Primary Tabs
                        val primaryTabs = listOf(
                            AppNavigationTab.DASHBOARD,
                            AppNavigationTab.FUNDS,
                            AppNavigationTab.TOOLS
                        )

                        primaryTabs.forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AccentCyan,
                                    selectedTextColor = AccentCyan,
                                    unselectedIconColor = if (isDark) Slate500 else Slate600,
                                    unselectedTextColor = if (isDark) Slate500 else Slate600,
                                    indicatorColor = AccentCyan.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag(tab.testTag)
                            )
                        }

                        // 4th Item: More Options
                        val isMoreSelected = currentTab in listOf(
                            AppNavigationTab.REPORTS,
                            AppNavigationTab.LIMITS,
                            AppNavigationTab.SETTINGS
                        )

                        NavigationBarItem(
                            selected = isMoreSelected,
                            onClick = { showMoreNavModal = true },
                            icon = {
                                Icon(
                                    imageVector = if (isMoreSelected) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                                    contentDescription = "More Options",
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = "More ⋯",
                                    fontSize = 11.sp,
                                    fontWeight = if (isMoreSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AccentCyan,
                                selectedTextColor = AccentCyan,
                                unselectedIconColor = if (isDark) Slate500 else Slate600,
                                unselectedTextColor = if (isDark) Slate500 else Slate600,
                                indicatorColor = AccentCyan.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_more")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                when (currentTab) {
                    AppNavigationTab.DASHBOARD -> {
                        DashboardScreen(
                            transactions = transactions,
                            limits = limits,
                            funds = funds,
                            selectedTimeframe = selectedTimeframe,
                            periodSummary = periodSummary,
                            budgetForecast = budgetForecast,
                            currencySymbol = currencySymbol,
                            onTimeframeChange = { viewModel.setTimeframe(it) },
                            onAddTransactionClick = {
                                selectedTransactionToEdit = null
                                showAddTransactionModal = true
                            },
                            onTransactionClick = { tx ->
                                selectedTransactionToEdit = tx
                                showAddTransactionModal = true
                            },
                            onNavigateToLimits = { currentTab = AppNavigationTab.LIMITS },
                            onExportCsvClick = { showExportCsvModal = true },
                            onSubscriptionsClick = { showSubscriptionsModal = true },
                            onGoalsClick = { currentTab = AppNavigationTab.FUNDS },
                            onPostPaymentNow = { recurringTx ->
                                viewModel.postRecurringPayment(recurringTx)
                            }
                        )
                    }
                    AppNavigationTab.EXPENSES -> {
                        ExpensesScreen(
                            templates = expenseTemplates,
                            transactions = transactions,
                            currencySymbol = currencySymbol,
                            onCreateTemplateClick = {
                                selectedTemplateToEdit = null
                                showCreateEditTemplateModal = true
                            },
                            onEditTemplateClick = { template ->
                                selectedTemplateToEdit = template
                                showCreateEditTemplateModal = true
                            },
                            onQuickLogTemplate = { template ->
                                viewModel.logTransactionFromTemplate(template)
                            },
                            onTogglePlanned = { template ->
                                viewModel.toggleExpenseTemplatePlanned(template)
                            },
                            onTransactionClick = { tx ->
                                selectedTransactionToEdit = tx
                                showAddTransactionModal = true
                            }
                        )
                    }
                    AppNavigationTab.FUNDS -> {
                        FundsScreen(
                            funds = funds,
                            transactions = transactions,
                            currencySymbol = currencySymbol,
                            onCreateFundClick = {
                                selectedFundToEdit = null
                                showCreateEditFundModal = true
                            },
                            onEditFundClick = { fund ->
                                selectedFundToEdit = fund
                                showCreateEditFundModal = true
                            },
                            onTopUpFundClick = { fund ->
                                selectedFundToTopUp = fund
                                showTopUpFundModal = true
                            },
                            onDeleteFundClick = { fund ->
                                viewModel.deleteFund(fund)
                            },
                            onQuickCreatePreset = { name, balance, target, colorHex, icon ->
                                viewModel.createFund(name, balance, target, colorHex, icon, "")
                            }
                        )
                    }
                    AppNavigationTab.TOOLS -> {
                        ToolsScreen(currencySymbol = currencySymbol)
                    }
                    AppNavigationTab.REPORTS -> {
                        ReportsScreen(
                            transactions = transactions,
                            limits = limits,
                            selectedTimeframe = selectedTimeframe,
                            periodSummary = periodSummary,
                            categoryBreakdown = categoryBreakdown,
                            barChartData = barChartData,
                            insights = insights,
                            currencySymbol = currencySymbol,
                            onTimeframeChange = { viewModel.setTimeframe(it) },
                            onExportCsvClick = { showExportCsvModal = true }
                        )
                    }
                    AppNavigationTab.LIMITS -> {
                        BudgetLimitsScreen(
                            limits = limits,
                            transactions = transactions,
                            currencySymbol = currencySymbol,
                            totalFundBalance = funds.sumOf { it.balance },
                            onAddLimitClick = {
                                selectedLimitToEdit = null
                                showSetBudgetModal = true
                            },
                            onEditLimitClick = { limit ->
                                selectedLimitToEdit = limit
                                showSetBudgetModal = true
                            },
                            onToggleLimit = { limit ->
                                viewModel.toggleBudgetLimit(limit)
                            },
                            onApplyAutoBudget = { allocations ->
                                viewModel.applyPlannedBudgetAllocations(allocations)
                            }
                        )
                    }
                    AppNavigationTab.SETTINGS -> {
                        SettingsScreen(
                            currencySymbol = currencySymbol,
                            notificationsEnabled = notificationsEnabled,
                            isPinLockEnabled = isPinLockEnabled,
                            currentPin = userPin,
                            transactionCount = transactions.size,
                            limitCount = limits.size,
                            themeMode = themeMode,
                            onThemeModeChange = { viewModel.setThemeMode(it) },
                            onCurrencyChange = { viewModel.setCurrency(it) },
                            onNotificationToggle = { viewModel.toggleNotifications(it) },
                            onSavePinSettings = { enabled, newPin -> viewModel.setPinSettings(enabled, newPin) },
                            onTestNotification = { viewModel.triggerTestNotification() },
                            onExportCsv = { showExportCsvModal = true },
                            onExportJson = { ctx -> viewModel.exportToJsonBackup(ctx) },
                            onRestoreJson = { json -> viewModel.restoreFromJson(json) },
                            onClearAllData = { viewModel.clearAllData() }
                        )
                    }
                }
            }
        }
    }

    // Add / Edit Transaction Modal BottomSheet
    if (showAddTransactionModal) {
        AddEditTransactionModal(
            transactionToEdit = selectedTransactionToEdit,
            funds = funds,
            currencySymbol = currencySymbol,
            onDismiss = {
                showAddTransactionModal = false
                selectedTransactionToEdit = null
            },
            onSave = { title, amount, category, type, note, paymentMethod, fundId, fundName, isRecurring, recurringInterval ->
                if (selectedTransactionToEdit == null) {
                    viewModel.addTransaction(
                        title = title,
                        amount = amount,
                        category = category,
                        type = type,
                        dateMillis = System.currentTimeMillis(),
                        note = note,
                        paymentMethod = paymentMethod,
                        fundId = fundId,
                        fundName = fundName,
                        isRecurring = isRecurring,
                        recurringInterval = recurringInterval
                    )
                } else {
                    val updated = selectedTransactionToEdit!!.copy(
                        title = title,
                        amount = amount,
                        category = category,
                        type = type,
                        note = note,
                        paymentMethod = paymentMethod,
                        fundId = fundId,
                        fundName = fundName,
                        isRecurring = isRecurring,
                        recurringInterval = recurringInterval
                    )
                    viewModel.updateTransaction(selectedTransactionToEdit, updated)
                }
            },
            onDelete = { tx ->
                viewModel.deleteTransaction(tx)
            }
        )
    }

    // Subscriptions Modal BottomSheet
    if (showSubscriptionsModal) {
        SubscriptionsModal(
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showSubscriptionsModal = false },
            onAddRecurringClick = {
                selectedTransactionToEdit = null
                showAddTransactionModal = true
            },
            onPostPaymentNow = { recurringTx ->
                viewModel.postRecurringPayment(recurringTx)
            }
        )
    }

    // Create / Edit Fund Modal BottomSheet
    if (showCreateEditFundModal) {
        CreateEditFundModal(
            fundToEdit = selectedFundToEdit,
            currencySymbol = currencySymbol,
            onDismiss = {
                showCreateEditFundModal = false
                selectedFundToEdit = null
            },
            onSave = { name, balance, target, colorHex, iconName, note ->
                if (selectedFundToEdit == null) {
                    viewModel.createFund(name, balance, target, colorHex, iconName, note)
                } else {
                    val updated = selectedFundToEdit!!.copy(
                        name = name,
                        balance = balance,
                        targetAmount = target,
                        colorHex = colorHex,
                        iconName = iconName,
                        note = note
                    )
                    viewModel.updateFund(updated)
                }
            },
            onDelete = { fund ->
                viewModel.deleteFund(fund)
            }
        )
    }

    // Top Up Fund Modal BottomSheet
    if (showTopUpFundModal && selectedFundToTopUp != null) {
        TopUpFundModal(
            fund = selectedFundToTopUp!!,
            currencySymbol = currencySymbol,
            onDismiss = {
                showTopUpFundModal = false
                selectedFundToTopUp = null
            },
            onConfirmTopUp = { amount, note, recordAsIncome ->
                viewModel.addMoneyToFund(
                    fund = selectedFundToTopUp!!,
                    amount = amount,
                    note = note,
                    recordAsIncome = recordAsIncome
                )
            }
        )
    }

    // Set / Edit Budget Limit Modal BottomSheet
    if (showSetBudgetModal) {
        SetBudgetModal(
            limitToEdit = selectedLimitToEdit,
            currencySymbol = currencySymbol,
            onDismiss = {
                showSetBudgetModal = false
                selectedLimitToEdit = null
            },
            onSave = { id, periodType, categoryName, amount, isEnabled, threshold ->
                viewModel.saveBudgetLimit(id, periodType, categoryName, amount, isEnabled, threshold)
            },
            onDelete = { limit ->
                viewModel.deleteBudgetLimit(limit)
            }
        )
    }

    // Export CSV Modal BottomSheet
    if (showExportCsvModal) {
        val context = LocalContext.current
        ExportCsvModal(
            transactions = transactions,
            currencySymbol = currencySymbol,
            onDismiss = { showExportCsvModal = false },
            onSaveToUri = { uri, csvContent ->
                viewModel.saveCsvToUri(context, uri, csvContent)
            },
            onSaveToDownloads = { csvContent, filename ->
                viewModel.saveCsvToDownloads(context, csvContent, filename)
            },
            onShare = { csvContent, filename ->
                viewModel.shareCsv(context, csvContent, filename)
            }
        )
    }

    // Create / Edit Expense Template Modal BottomSheet
    if (showCreateEditTemplateModal) {
        CreateEditTemplateModal(
            templateToEdit = selectedTemplateToEdit,
            currencySymbol = currencySymbol,
            onDismiss = {
                showCreateEditTemplateModal = false
                selectedTemplateToEdit = null
            },
            onSave = { title, category, amount, paymentMethod, note, frequency, isPlanned, iconName, colorHex ->
                if (selectedTemplateToEdit == null) {
                    viewModel.createExpenseTemplate(
                        title = title,
                        category = category,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        note = note,
                        frequency = frequency,
                        isPlanned = isPlanned,
                        iconName = iconName,
                        colorHex = colorHex
                    )
                } else {
                    val updated = selectedTemplateToEdit!!.copy(
                        title = title,
                        category = category,
                        amount = amount,
                        paymentMethod = paymentMethod,
                        note = note,
                        frequency = frequency,
                        isPlanned = isPlanned,
                        iconName = iconName,
                        colorHex = colorHex
                    )
                    viewModel.updateExpenseTemplate(updated)
                }
            },
            onDelete = { template ->
                viewModel.deleteExpenseTemplate(template)
            }
        )
    }

    // More Navigation Modal BottomSheet
    if (showMoreNavModal) {
        MoreNavigationModal(
            currentTab = currentTab,
            onSelectTab = { tab ->
                currentTab = tab
            },
            onOpenExportCsv = { showExportCsvModal = true },
            onOpenSubscriptions = { showSubscriptionsModal = true },
            onDismiss = { showMoreNavModal = false }
        )
    }
    }
}

@Composable
private fun Slate950Alpha(): Color {
    return Color(0xE60F172A)
}
