package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.DebtEntity
import com.example.data.local.entity.ExpenseTemplateEntity
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.model.CategoryRegistry
import com.example.data.repository.BudgetRepository
import com.example.notification.NotificationHelper
import com.example.ui.components.BarChartItem
import com.example.ui.components.CategorySpendItem
import com.example.ui.components.MonthlyTrendVsLimitItem
import com.example.util.BackupExportHelper
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TimeframeFilter(val displayName: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly")
}

data class SpendingInsight(
    val title: String,
    val description: String,
    val type: InsightType, // POSITIVE, WARNING, DANGER, INFO
    val iconCategory: String? = null
)

enum class InsightType {
    POSITIVE,
    WARNING,
    DANGER,
    INFO
}

data class PeriodSummary(
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val netBalance: Double = 0.0,
    val limitAmount: Double = 0.0,
    val limitRatio: Float = 0f,
    val isLimitExceeded: Boolean = false,
    val isLimitWarning: Boolean = false
) {
    val totalExpenses: Double get() = totalExpense
    val remainingAmount: Double get() = (limitAmount - totalExpense).coerceAtLeast(0.0)
}

enum class ForecastHealth {
    ON_TRACK,
    CAUTION,
    OVER_BUDGET
}

data class BudgetForecast(
    val currentSpend: Double = 0.0,
    val currentIncome: Double = 0.0,
    val totalBudgetLimit: Double = 0.0,
    val daysElapsedInMonth: Int = 1,
    val daysInMonth: Int = 30,
    val daysRemaining: Int = 29,
    val averageDailySpend: Double = 0.0,
    val projectedTotalExpense: Double = 0.0,
    val projectedRemainingBalance: Double = 0.0,
    val recommendedDailyAllowance: Double = 0.0,
    val healthStatus: ForecastHealth = ForecastHealth.ON_TRACK
)

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val notificationHelper = NotificationHelper(application)
    private val repository = BudgetRepository(db.transactionDao(), db.budgetLimitDao(), db.fundDao(), db.expenseTemplateDao(), db.debtDao(), notificationHelper)

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgetLimits: StateFlow<List<BudgetLimitEntity>> = repository.allLimits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val funds: StateFlow<List<FundEntity>> = repository.allFunds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseTemplates: StateFlow<List<ExpenseTemplateEntity>> = repository.allTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Seed initial default expense templates and debts if empty
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            if (expenseTemplates.value.isEmpty()) {
                seedInitialTemplates()
            }
            if (debts.value.isEmpty()) {
                seedInitialDebts()
            }
        }
    }

    private suspend fun seedInitialTemplates() {
        val defaultTemplates = listOf(
            ExpenseTemplateEntity(
                title = "Weekly Groceries",
                category = "Food & Dining",
                amount = 120.00,
                paymentMethod = "Credit Card",
                note = "Supermarket & pantry restock",
                frequency = "WEEKLY",
                isPlanned = true,
                iconName = "ShoppingBag",
                colorHex = "#10B981"
            ),
            ExpenseTemplateEntity(
                title = "Monthly Utility Bill",
                category = "Utilities",
                amount = 85.00,
                paymentMethod = "Bank Transfer",
                note = "Electric & Water",
                frequency = "MONTHLY",
                isPlanned = true,
                iconName = "Receipt",
                colorHex = "#3B82F6"
            ),
            ExpenseTemplateEntity(
                title = "Gas & Commute",
                category = "Transportation",
                amount = 45.00,
                paymentMethod = "Credit Card",
                note = "Vehicle fuel top-up",
                frequency = "WEEKLY",
                isPlanned = true,
                iconName = "LocalGasStation",
                colorHex = "#F59E0B"
            ),
            ExpenseTemplateEntity(
                title = "Coffee & Snacks",
                category = "Food & Dining",
                amount = 15.00,
                paymentMethod = "Apple Pay / Wallet",
                note = "Daily espresso & pastries",
                frequency = "DAILY",
                isPlanned = false,
                iconName = "Fastfood",
                colorHex = "#EC4899"
            ),
            ExpenseTemplateEntity(
                title = "Gym Membership",
                category = "Health & Fitness",
                amount = 35.00,
                paymentMethod = "Debit Card",
                note = "Fitness center recurring access",
                frequency = "MONTHLY",
                isPlanned = true,
                iconName = "FitnessCenter",
                colorHex = "#8B5CF6"
            )
        )
        defaultTemplates.forEach { repository.insertTemplate(it) }
    }

    private suspend fun seedInitialDebts() {
        val now = System.currentTimeMillis()
        val defaultDebts = listOf(
            DebtEntity(
                personName = "Sarah Connor",
                type = "OWED_TO_YOU",
                totalAmount = 85.00,
                amountPaid = 25.00,
                dueDateMillis = now + (5 * 24 * 60 * 60 * 1000L), // due in 5 days
                notes = "Dinner & concert ticket split",
                contactPhone = "+1 555-0192",
                isSettled = false
            ),
            DebtEntity(
                personName = "Alex Rivera",
                type = "OWED_TO_YOU",
                totalAmount = 150.00,
                amountPaid = 0.0,
                dueDateMillis = now + (12 * 24 * 60 * 60 * 1000L),
                notes = "Lent cash for weekend roadtrip",
                contactPhone = "+1 555-0144",
                isSettled = false
            ),
            DebtEntity(
                personName = "Tech Store (Laptop)",
                type = "YOU_OWE",
                totalAmount = 400.00,
                amountPaid = 200.00,
                dueDateMillis = now + (20 * 24 * 60 * 60 * 1000L),
                notes = "Remaining balance on device loan",
                contactPhone = "",
                isSettled = false
            )
        )
        defaultDebts.forEach { repository.insertDebt(it) }
    }

    val budgetForecast: StateFlow<BudgetForecast> = combine(
        transactions,
        budgetLimits
    ) { txs, limits ->
        calculateBudgetForecast(txs, limits)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BudgetForecast())

    private val _selectedTimeframe = MutableStateFlow(TimeframeFilter.MONTHLY)
    val selectedTimeframe: StateFlow<TimeframeFilter> = _selectedTimeframe.asStateFlow()

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(prefs.getString("pref_theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _currencySymbol = MutableStateFlow("$")
    val currencySymbol: StateFlow<String> = _currencySymbol.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Security PIN Lock State
    private val _isPinLockEnabled = MutableStateFlow(false)
    val isPinLockEnabled: StateFlow<Boolean> = _isPinLockEnabled.asStateFlow()

    private val _userPin = MutableStateFlow("1234")
    val userPin: StateFlow<String> = _userPin.asStateFlow()

    private val _isAppUnlocked = MutableStateFlow(true)
    val isAppUnlocked: StateFlow<Boolean> = _isAppUnlocked.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("pref_theme_mode", mode.name).apply()
        _statusMessage.value = "Theme updated to ${mode.displayName}"
    }

    fun setTimeframe(timeframe: TimeframeFilter) {
        _selectedTimeframe.value = timeframe
    }

    fun setCurrency(symbol: String) {
        _currencySymbol.value = symbol
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun setPinSettings(enabled: Boolean, newPin: String) {
        _isPinLockEnabled.value = enabled
        if (newPin.length == 4) {
            _userPin.value = newPin
        }
        if (!enabled) {
            _isAppUnlocked.value = true
        } else {
            _statusMessage.value = "PIN Lock protection enabled"
        }
    }

    fun unlockApp() {
        _isAppUnlocked.value = true
    }

    fun lockApp() {
        if (_isPinLockEnabled.value) {
            _isAppUnlocked.value = false
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        type: String,
        dateMillis: Long,
        note: String,
        paymentMethod: String,
        fundId: Long? = null,
        fundName: String? = null,
        isRecurring: Boolean = false,
        recurringInterval: String = "MONTHLY"
    ) {
        viewModelScope.launch {
            val entity = TransactionEntity(
                title = title,
                amount = amount,
                category = category,
                type = type,
                dateMillis = dateMillis,
                note = note,
                paymentMethod = paymentMethod,
                fundId = fundId,
                fundName = fundName,
                isRecurring = isRecurring,
                recurringInterval = recurringInterval
            )
            repository.insertTransaction(entity)
            _statusMessage.value = if (isRecurring) "Recurring bill saved" else "Transaction recorded"
        }
    }

    fun postRecurringPayment(recurringTx: TransactionEntity) {
        viewModelScope.launch {
            val paymentTx = TransactionEntity(
                title = recurringTx.title,
                amount = recurringTx.amount,
                category = recurringTx.category,
                type = recurringTx.type,
                dateMillis = System.currentTimeMillis(),
                note = "Auto-posted subscription payment: ${recurringTx.note}",
                paymentMethod = recurringTx.paymentMethod,
                fundId = recurringTx.fundId,
                fundName = recurringTx.fundName,
                isRecurring = false
            )
            repository.insertTransaction(paymentTx)
            _statusMessage.value = "Payment recorded for ${recurringTx.title}"
        }
    }

    fun updateTransaction(oldTransaction: TransactionEntity?, newTransaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(oldTransaction, newTransaction)
            _statusMessage.value = "Transaction updated"
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _statusMessage.value = "Transaction deleted"
        }
    }

    // Funds Operations
    fun createFund(
        name: String,
        initialBalance: Double,
        targetAmount: Double?,
        colorHex: String,
        iconName: String,
        note: String
    ) {
        viewModelScope.launch {
            // Set initial fund balance to 0.0 if we log an initial deposit transaction,
            // because repository.insertTransaction(initTx) adds initialBalance to the fund balance.
            val startingBalance = if (initialBalance > 0) 0.0 else initialBalance
            val fund = FundEntity(
                name = name,
                balance = startingBalance,
                targetAmount = targetAmount,
                colorHex = colorHex,
                iconName = iconName,
                note = note
            )
            val fundId = repository.insertFund(fund)
            
            // If starting with a positive balance, log an initial funding income transaction
            if (initialBalance > 0) {
                val initTx = TransactionEntity(
                    title = "Initial Deposit - $name",
                    amount = initialBalance,
                    category = "Salary & Income",
                    type = "INCOME",
                    note = "Initial fund creation deposit",
                    paymentMethod = "Bank Transfer",
                    fundId = fundId,
                    fundName = name
                )
                repository.insertTransaction(initTx)
            }
            _statusMessage.value = "Fund '$name' created"
        }
    }

    fun updateFund(fund: FundEntity) {
        viewModelScope.launch {
            repository.updateFund(fund)
            _statusMessage.value = "Fund '${fund.name}' updated"
        }
    }

    fun deleteFund(fund: FundEntity) {
        viewModelScope.launch {
            repository.deleteFund(fund)
            _statusMessage.value = "Fund deleted"
        }
    }

    // Expense Template Actions
    fun createExpenseTemplate(
        title: String,
        category: String,
        amount: Double,
        paymentMethod: String,
        note: String,
        frequency: String,
        isPlanned: Boolean,
        iconName: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            val template = ExpenseTemplateEntity(
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
            repository.insertTemplate(template)
            _statusMessage.value = "Expense template '$title' saved!"
        }
    }

    fun updateExpenseTemplate(template: ExpenseTemplateEntity) {
        viewModelScope.launch {
            repository.updateTemplate(template)
            _statusMessage.value = "Expense template '${template.title}' updated"
        }
    }

    fun deleteExpenseTemplate(template: ExpenseTemplateEntity) {
        viewModelScope.launch {
            repository.deleteTemplate(template)
            _statusMessage.value = "Expense template removed"
        }
    }

    fun toggleExpenseTemplatePlanned(template: ExpenseTemplateEntity) {
        viewModelScope.launch {
            val updated = template.copy(isPlanned = !template.isPlanned)
            repository.updateTemplate(updated)
            _statusMessage.value = if (updated.isPlanned) "'${template.title}' added to month plan" else "'${template.title}' unflagged from plan"
        }
    }

    fun logTransactionFromTemplate(template: ExpenseTemplateEntity) {
        viewModelScope.launch {
            val tx = TransactionEntity(
                title = template.title,
                amount = template.amount,
                category = template.category,
                type = "EXPENSE",
                dateMillis = System.currentTimeMillis(),
                note = if (template.note.isNotBlank()) "Recorded via template: ${template.note}" else "Recorded via template",
                paymentMethod = template.paymentMethod
            )
            repository.insertTransaction(tx)
            _statusMessage.value = "⚡ Recorded ${template.title} (${_currencySymbol.value}${String.format(Locale.US, "%.2f", template.amount)})"
        }
    }

    // Debts & Debtors Actions
    fun createDebt(
        personName: String,
        type: String,
        totalAmount: Double,
        amountPaid: Double = 0.0,
        dueDateMillis: Long? = null,
        notes: String = "",
        contactPhone: String = ""
    ) {
        viewModelScope.launch {
            val debt = DebtEntity(
                personName = personName,
                type = type,
                totalAmount = totalAmount,
                amountPaid = amountPaid.coerceIn(0.0, totalAmount),
                dueDateMillis = dueDateMillis,
                notes = notes,
                contactPhone = contactPhone,
                isSettled = amountPaid >= totalAmount
            )
            repository.insertDebt(debt)
            val typeLabel = if (type == "OWED_TO_YOU") "debtor" else "debt"
            _statusMessage.value = "Added $typeLabel for $personName"
        }
    }

    fun updateDebt(debt: DebtEntity) {
        viewModelScope.launch {
            val autoSettled = debt.amountPaid >= debt.totalAmount
            val updated = debt.copy(isSettled = autoSettled || debt.isSettled)
            repository.updateDebt(updated)
            _statusMessage.value = "Debt record updated"
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
            _statusMessage.value = "Debt record removed"
        }
    }

    fun recordDebtPayment(debt: DebtEntity, paymentAmount: Double, recordAsTransaction: Boolean = true) {
        viewModelScope.launch {
            val newAmountPaid = (debt.amountPaid + paymentAmount).coerceAtMost(debt.totalAmount)
            val isNowSettled = newAmountPaid >= debt.totalAmount
            val updated = debt.copy(amountPaid = newAmountPaid, isSettled = isNowSettled)
            repository.updateDebt(updated)

            if (recordAsTransaction) {
                val isIncome = debt.type == "OWED_TO_YOU"
                val tx = TransactionEntity(
                    title = if (isIncome) "Repayment from ${debt.personName}" else "Debt payment to ${debt.personName}",
                    amount = paymentAmount,
                    category = if (isIncome) "Salary & Income" else "Utilities",
                    type = if (isIncome) "INCOME" else "EXPENSE",
                    note = "Recorded payment for debt: ${debt.notes.ifBlank { debt.personName }}",
                    paymentMethod = "Bank Transfer"
                )
                repository.insertTransaction(tx)
            }

            _statusMessage.value = "Recorded ${_currencySymbol.value}${String.format(Locale.US, "%.2f", paymentAmount)} payment for ${debt.personName}"
        }
    }

    fun toggleDebtSettled(debt: DebtEntity) {
        viewModelScope.launch {
            val newSettled = !debt.isSettled
            val newPaid = if (newSettled) debt.totalAmount else debt.amountPaid
            val updated = debt.copy(isSettled = newSettled, amountPaid = newPaid)
            repository.updateDebt(updated)
            _statusMessage.value = if (newSettled) "${debt.personName} debt marked settled!" else "Reopened debt for ${debt.personName}"
        }
    }

    fun addMoneyToFund(
        fund: FundEntity,
        amount: Double,
        note: String = "",
        recordAsIncome: Boolean = true
    ) {
        viewModelScope.launch {
            if (recordAsIncome) {
                val tx = TransactionEntity(
                    title = "Top-up: ${fund.name}",
                    amount = amount,
                    category = "Salary & Income",
                    type = "INCOME",
                    note = note.ifBlank { "Top-up added to fund" },
                    paymentMethod = "Bank Transfer",
                    fundId = fund.id,
                    fundName = fund.name
                )
                repository.insertTransaction(tx)
            } else {
                repository.adjustFundBalance(fund.id, amount)
            }
            _statusMessage.value = "Added ${_currencySymbol.value}${String.format(Locale.US, "%.2f", amount)} to ${fund.name}"
        }
    }

    fun setFundBalance(fundId: Long, newBalance: Double) {
        viewModelScope.launch {
            repository.setFundBalance(fundId, newBalance)
            _statusMessage.value = "Fund balance updated"
        }
    }

    fun saveBudgetLimit(
        id: Long = 0,
        periodType: String,
        categoryName: String? = null,
        limitAmount: Double,
        isEnabled: Boolean = true,
        notifyThresholdPercent: Int = 80
    ) {
        viewModelScope.launch {
            val entity = BudgetLimitEntity(
                id = id,
                periodType = periodType,
                categoryName = categoryName,
                limitAmount = limitAmount,
                isEnabled = isEnabled,
                notifyThresholdPercent = notifyThresholdPercent
            )
            repository.insertOrUpdateLimit(entity)
            _statusMessage.value = "Budget limit saved"
        }
    }

    fun applyPlannedBudgetAllocations(allocations: Map<String, Double>) {
        viewModelScope.launch {
            val currentLimits = repository.allLimits.first()
            allocations.forEach { (categoryName, amount) ->
                val existing = currentLimits.find { it.periodType == "CATEGORY" && it.categoryName == categoryName }
                val entity = BudgetLimitEntity(
                    id = existing?.id ?: 0,
                    periodType = "CATEGORY",
                    categoryName = categoryName,
                    limitAmount = amount,
                    isEnabled = true,
                    notifyThresholdPercent = 80
                )
                repository.insertOrUpdateLimit(entity)
            }
            _statusMessage.value = "Smart auto-budget limits applied!"
        }
    }

    fun deleteBudgetLimit(limit: BudgetLimitEntity) {
        viewModelScope.launch {
            repository.deleteLimit(limit)
            _statusMessage.value = "Budget limit removed"
        }
    }

    fun toggleBudgetLimit(limit: BudgetLimitEntity) {
        viewModelScope.launch {
            val updated = limit.copy(isEnabled = !limit.isEnabled)
            repository.insertOrUpdateLimit(updated)
        }
    }

    fun triggerTestNotification() {
        notificationHelper.sendBudgetAlert(
            title = "🔔 Budget Alert Simulation",
            message = "Daily budget is at 90% ($58.50 / $65.00 limit). Tap to review your spend insights.",
            isWarningOnly = true
        )
        _statusMessage.value = "Test notification dispatched"
    }

    fun generateCsvContent(
        transactions: List<TransactionEntity>,
        timeframe: TimeframeFilter? = null,
        typeFilter: String? = null
    ): String {
        var filtered = if (timeframe != null) getFilteredTransactions(transactions, timeframe) else transactions
        if (typeFilter != null && typeFilter != "ALL") {
            filtered = filtered.filter { it.type == typeFilter }
        }
        return BackupExportHelper.generateCsv(filtered, _currencySymbol.value)
    }

    fun saveCsvToUri(context: Context, uri: android.net.Uri, csvContent: String) {
        viewModelScope.launch {
            val success = BackupExportHelper.saveCsvToUri(context, uri, csvContent)
            if (success) {
                _statusMessage.value = "Local CSV backup saved successfully!"
            } else {
                _statusMessage.value = "Failed to save CSV file."
            }
        }
    }

    fun saveCsvToDownloads(context: Context, csvContent: String, filename: String) {
        viewModelScope.launch {
            val success = BackupExportHelper.saveCsvToDownloads(context, csvContent, filename)
            if (success) {
                _statusMessage.value = "Saved CSV backup to Downloads folder!"
            } else {
                _statusMessage.value = "Could not save to Downloads. Please use file picker."
            }
        }
    }

    fun shareCsv(context: Context, csvContent: String, filename: String) {
        viewModelScope.launch {
            BackupExportHelper.shareCsvFile(context, csvContent, filename)
            _statusMessage.value = "CSV Backup ready to share/save"
        }
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            val list = repository.getAllTransactionsSnapshot()
            val csv = BackupExportHelper.generateCsv(list, _currencySymbol.value)
            val filename = "budget_expenses_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            BackupExportHelper.shareCsvFile(context, csv, filename)
            _statusMessage.value = "CSV Backup generated"
        }
    }

    fun exportToJsonBackup(context: Context) {
        viewModelScope.launch {
            val list = repository.getAllTransactionsSnapshot()
            val limits = repository.getAllLimitsSnapshot()
            val json = BackupExportHelper.generateJsonBackup(list, limits)
            val filename = "budget_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
            BackupExportHelper.shareTextFile(context, json, filename, "application/json")
            _statusMessage.value = "JSON Backup generated"
        }
    }

    fun restoreFromJson(jsonString: String) {
        viewModelScope.launch {
            try {
                val parsed = BackupExportHelper.parseJsonBackup(jsonString)
                repository.restoreDatabase(parsed.transactions, parsed.limits)
                _statusMessage.value = "Restored ${parsed.transactions.size} records & ${parsed.limits.size} limits successfully!"
            } catch (e: Exception) {
                _statusMessage.value = "Restore failed: Invalid JSON file format."
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _statusMessage.value = "All transaction records wiped"
        }
    }

    // --- Helper calculations for UI views ---

    fun getFilteredTransactions(
        all: List<TransactionEntity>,
        timeframe: TimeframeFilter
    ): List<TransactionEntity> {
        val startMillis = getStartMillisForTimeframe(timeframe)
        return all.filter { it.dateMillis >= startMillis }
    }

    fun getPeriodSummary(
        all: List<TransactionEntity>,
        limits: List<BudgetLimitEntity>,
        timeframe: TimeframeFilter
    ): PeriodSummary {
        val filtered = getFilteredTransactions(all, timeframe)
        val expense = filtered.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val income = filtered.filter { it.type == "INCOME" }.sumOf { it.amount }
        val net = income - expense

        val periodTypeStr = when (timeframe) {
            TimeframeFilter.DAILY -> "DAILY"
            TimeframeFilter.WEEKLY -> "WEEKLY"
            TimeframeFilter.MONTHLY -> "MONTHLY"
            TimeframeFilter.YEARLY -> "YEARLY"
        }

        val limitObj = limits.find { it.periodType == periodTypeStr && it.isEnabled }
        val limitAmount = limitObj?.limitAmount ?: 0.0
        val ratio = if (limitAmount > 0) (expense / limitAmount).toFloat() else 0f
        val threshold = (limitObj?.notifyThresholdPercent ?: 80) / 100f

        return PeriodSummary(
            totalExpense = expense,
            totalIncome = income,
            netBalance = net,
            limitAmount = limitAmount,
            limitRatio = ratio,
            isLimitExceeded = limitAmount > 0 && expense > limitAmount,
            isLimitWarning = limitAmount > 0 && ratio >= threshold && expense <= limitAmount
        )
    }

    fun getCategorySpendBreakdown(
        all: List<TransactionEntity>,
        timeframe: TimeframeFilter
    ): List<CategorySpendItem> {
        val expenses = getFilteredTransactions(all, timeframe).filter { it.type == "EXPENSE" }
        val total = expenses.sumOf { it.amount }
        if (total <= 0) return emptyList()

        return expenses
            .groupBy { it.category }
            .map { (cat, list) ->
                val amount = list.sumOf { it.amount }
                val catMeta = CategoryRegistry.getCategory(cat)
                CategorySpendItem(
                    category = cat,
                    amount = amount,
                    percentage = ((amount / total) * 100).toFloat(),
                    color = catMeta.color
                )
            }
            .sortedByDescending { it.amount }
    }

    fun getBarChartData(
        all: List<TransactionEntity>,
        timeframe: TimeframeFilter
    ): List<BarChartItem> {
        val expenses = all.filter { it.type == "EXPENSE" }
        val cal = Calendar.getInstance()

        return when (timeframe) {
            TimeframeFilter.DAILY -> {
                // Last 7 days
                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                val list = mutableListOf<BarChartItem>()
                for (i in 6 downTo 0) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.DAY_OF_YEAR, -i)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    c.set(Calendar.SECOND, 0)
                    c.set(Calendar.MILLISECOND, 0)
                    val dayStart = c.timeInMillis
                    c.add(Calendar.DAY_OF_YEAR, 1)
                    val dayEnd = c.timeInMillis

                    val daySpend = expenses.filter { it.dateMillis in dayStart until dayEnd }.sumOf { it.amount }
                    val label = if (i == 0) "Today" else dayFormat.format(Date(dayStart))
                    list.add(BarChartItem(label = label, amount = daySpend, isHighlighted = (i == 0)))
                }
                list
            }
            TimeframeFilter.WEEKLY -> {
                // Last 4 weeks
                val list = mutableListOf<BarChartItem>()
                for (w in 3 downTo 0) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.WEEK_OF_YEAR, -w)
                    c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    val weekStart = c.timeInMillis
                    c.add(Calendar.WEEK_OF_YEAR, 1)
                    val weekEnd = c.timeInMillis

                    val weekSpend = expenses.filter { it.dateMillis in weekStart until weekEnd }.sumOf { it.amount }
                    val label = if (w == 0) "This Wk" else "Wk -${w}"
                    list.add(BarChartItem(label = label, amount = weekSpend, isHighlighted = (w == 0)))
                }
                list
            }
            TimeframeFilter.MONTHLY -> {
                // Last 6 months
                val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                val list = mutableListOf<BarChartItem>()
                for (m in 5 downTo 0) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.MONTH, -m)
                    c.set(Calendar.DAY_OF_MONTH, 1)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    c.set(Calendar.MINUTE, 0)
                    val monthStart = c.timeInMillis
                    c.add(Calendar.MONTH, 1)
                    val monthEnd = c.timeInMillis

                    val monthSpend = expenses.filter { it.dateMillis in monthStart until monthEnd }.sumOf { it.amount }
                    val label = if (m == 0) "This Mo" else monthFormat.format(Date(monthStart))
                    list.add(BarChartItem(label = label, amount = monthSpend, isHighlighted = (m == 0)))
                }
                list
            }
            TimeframeFilter.YEARLY -> {
                // Last 4 years
                val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
                val list = mutableListOf<BarChartItem>()
                for (y in 3 downTo 0) {
                    val c = Calendar.getInstance()
                    c.add(Calendar.YEAR, -y)
                    c.set(Calendar.DAY_OF_YEAR, 1)
                    c.set(Calendar.HOUR_OF_DAY, 0)
                    val yearStart = c.timeInMillis
                    c.add(Calendar.YEAR, 1)
                    val yearEnd = c.timeInMillis

                    val yearSpend = expenses.filter { it.dateMillis in yearStart until yearEnd }.sumOf { it.amount }
                    val label = if (y == 0) "This Yr" else yearFormat.format(Date(yearStart))
                    list.add(BarChartItem(label = label, amount = yearSpend, isHighlighted = (y == 0)))
                }
                list
            }
        }
    }

    fun generateInsights(
        all: List<TransactionEntity>,
        limits: List<BudgetLimitEntity>,
        timeframe: TimeframeFilter
    ): List<SpendingInsight> {
        val insights = mutableListOf<SpendingInsight>()
        val filtered = getFilteredTransactions(all, timeframe)
        val expenses = filtered.filter { it.type == "EXPENSE" }
        val income = filtered.filter { it.type == "INCOME" }
        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = income.sumOf { it.amount }

        if (expenses.isEmpty()) {
            insights.add(
                SpendingInsight(
                    title = "No Expense Records",
                    description = "Add your first expense transaction to generate personalized spending insights.",
                    type = InsightType.INFO
                )
            )
            return insights
        }

        // 1. Month-over-Month (MoM) Category Comparison Insight
        val nowCal = Calendar.getInstance()
        val curMonthStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val prevMonthStart = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val curMonthExpenses = all.filter { it.type == "EXPENSE" && it.dateMillis >= curMonthStart }
        val prevMonthExpenses = all.filter { it.type == "EXPENSE" && it.dateMillis in prevMonthStart until curMonthStart }

        val curCatMap = curMonthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { tx -> tx.amount } }
        val prevCatMap = prevMonthExpenses.groupBy { it.category }.mapValues { it.value.sumOf { tx -> tx.amount } }

        // Find category with biggest MoM percentage increase or notable change
        var momInsightAdded = false
        val allCatKeys = (curCatMap.keys + prevCatMap.keys).distinct()
        for (catId in allCatKeys) {
            val curAmt = curCatMap[catId] ?: 0.0
            val prevAmt = prevCatMap[catId] ?: 0.0
            val catMeta = com.example.data.model.CategoryRegistry.getCategory(catId)

            if (prevAmt > 0 && curAmt > 0) {
                val pctChange = (((curAmt - prevAmt) / prevAmt) * 100).toInt()
                if (pctChange >= 15) {
                    insights.add(
                        SpendingInsight(
                            title = "Month-over-Month Increase: ${catMeta.name}",
                            description = "You spent $pctChange% more on ${catMeta.name.lowercase()} this month compared to last month (${_currencySymbol.value}${String.format(Locale.US, "%.2f", curAmt)} vs ${_currencySymbol.value}${String.format(Locale.US, "%.2f", prevAmt)}).",
                            type = if (pctChange > 30) InsightType.WARNING else InsightType.INFO,
                            iconCategory = catId
                        )
                    )
                    momInsightAdded = true
                    break
                } else if (pctChange <= -15) {
                    insights.add(
                        SpendingInsight(
                            title = "Smart Savings: ${catMeta.name}",
                            description = "Great job! You spent ${kotlin.math.abs(pctChange)}% less on ${catMeta.name.lowercase()} this month compared to last month (${_currencySymbol.value}${String.format(Locale.US, "%.2f", curAmt)} vs ${_currencySymbol.value}${String.format(Locale.US, "%.2f", prevAmt)}).",
                            type = InsightType.POSITIVE,
                            iconCategory = catId
                        )
                    )
                    momInsightAdded = true
                    break
                }
            }
        }

        // 2. Year-to-Date (YTD) Macro Insight
        val ytdStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val ytdExpenses = all.filter { it.type == "EXPENSE" && it.dateMillis >= ytdStart }
        val ytdTotal = ytdExpenses.sumOf { it.amount }
        if (ytdTotal > 0) {
            val ytdTopCatGroup = ytdExpenses.groupBy { it.category }
                .mapValues { it.value.sumOf { tx -> tx.amount } }
                .maxByOrNull { it.value }

            if (ytdTopCatGroup != null) {
                val catMeta = com.example.data.model.CategoryRegistry.getCategory(ytdTopCatGroup.key)
                val ytdPct = ((ytdTopCatGroup.value / ytdTotal) * 100).toInt()
                insights.add(
                    SpendingInsight(
                        title = "Biggest Expense Category This Year",
                        description = "Your biggest expense category this year is ${catMeta.name} (${_currencySymbol.value}${String.format(Locale.US, "%,.2f", ytdTopCatGroup.value)}, $ytdPct% of total YTD spending).",
                        type = InsightType.INFO,
                        iconCategory = ytdTopCatGroup.key
                    )
                )
            }
        }

        // 3. Top Spending Category in Selected Timeframe
        val catGroups = expenses.groupBy { it.category }
            .mapValues { it.value.sumOf { tx -> tx.amount } }
            .toList()
            .sortedByDescending { it.second }

        val topCat = catGroups.firstOrNull()
        if (topCat != null && totalExpense > 0) {
            val catMeta = com.example.data.model.CategoryRegistry.getCategory(topCat.first)
            val pct = ((topCat.second / totalExpense) * 100).toInt()
            insights.add(
                SpendingInsight(
                    title = "Period Leader: ${catMeta.name}",
                    description = "${catMeta.name} accounts for $pct% (${_currencySymbol.value}${String.format(Locale.US, "%.2f", topCat.second)}) of all spending in this timeframe.",
                    type = if (pct > 40) InsightType.WARNING else InsightType.INFO,
                    iconCategory = topCat.first
                )
            )

            // AI Actionable Optimization Tip
            if (pct > 20) {
                val potentialSaving = topCat.second * 0.10
                insights.add(
                    SpendingInsight(
                        title = "AI Optimization Suggestion",
                        description = "Reducing spending on ${catMeta.name} by 10% could save you ${_currencySymbol.value}${String.format(Locale.US, "%.2f", potentialSaving)} per period towards your savings goals.",
                        type = InsightType.POSITIVE,
                        iconCategory = topCat.first
                    )
                )
            }
        }

        // 4. Peak Day Spending Habit
        val dayOfWeekFormat = java.text.SimpleDateFormat("EEEE", Locale.US)
        val dayGroups = expenses.groupBy {
            val c = Calendar.getInstance().apply { timeInMillis = it.dateMillis }
            c.get(Calendar.DAY_OF_WEEK)
        }.mapValues { it.value.sumOf { tx -> tx.amount } }

        val peakDayEntry = dayGroups.maxByOrNull { it.value }
        if (peakDayEntry != null && totalExpense > 50) {
            val dayName = when (peakDayEntry.key) {
                Calendar.SUNDAY -> "Sundays"
                Calendar.MONDAY -> "Mondays"
                Calendar.TUESDAY -> "Tuesdays"
                Calendar.WEDNESDAY -> "Wednesdays"
                Calendar.THURSDAY -> "Thursdays"
                Calendar.FRIDAY -> "Fridays"
                Calendar.SATURDAY -> "Saturdays"
                else -> "Weekends"
            }
            val peakPct = ((peakDayEntry.value / totalExpense) * 100).toInt()
            insights.add(
                SpendingInsight(
                    title = "Peak Spending Day: $dayName",
                    description = "$dayName are your highest spending day, making up $peakPct% (${_currencySymbol.value}${String.format(Locale.US, "%.2f", peakDayEntry.value)}) of total expenses.",
                    type = InsightType.INFO
                )
            )
        }

        // 5. Limit Breach Check Insights
        val summary = getPeriodSummary(all, limits, timeframe)
        if (summary.isLimitExceeded) {
            val over = summary.totalExpense - summary.limitAmount
            insights.add(
                SpendingInsight(
                    title = "Budget Limit Exceeded!",
                    description = "You are ${_currencySymbol.value}${String.format(Locale.US, "%.2f", over)} over your ${timeframe.displayName.lowercase()} budget ceiling of ${_currencySymbol.value}${String.format(Locale.US, "%.2f", summary.limitAmount)}.",
                    type = InsightType.DANGER
                )
            )
        } else if (summary.isLimitWarning) {
            val pct = (summary.limitRatio * 100).toInt()
            insights.add(
                SpendingInsight(
                    title = "Approaching Limit ($pct%)",
                    description = "You've used $pct% of your budget limit. Consider slowing down discretionary purchases.",
                    type = InsightType.WARNING
                )
            )
        } else if (summary.limitAmount > 0) {
            val saved = summary.limitAmount - summary.totalExpense
            insights.add(
                SpendingInsight(
                    title = "Healthy Financial Track",
                    description = "You have saved ${_currencySymbol.value}${String.format(Locale.US, "%.2f", saved)} within your limit so far.",
                    type = InsightType.POSITIVE
                )
            )
        }

        // 6. Savings Rate
        if (totalIncome > 0) {
            val savingsRate = (((totalIncome - totalExpense) / totalIncome) * 100).toInt()
            if (savingsRate >= 20) {
                insights.add(
                    SpendingInsight(
                        title = "Strong Savings Rate ($savingsRate%)",
                        description = "Your net cashflow retention is currently above the 20% benchmark recommended by financial advisors.",
                        type = InsightType.POSITIVE
                    )
                )
            } else if (savingsRate < 0) {
                insights.add(
                    SpendingInsight(
                        title = "Negative Cash Flow",
                        description = "Expenses exceed income by ${_currencySymbol.value}${String.format(Locale.US, "%.2f", totalExpense - totalIncome)} for this timeframe.",
                        type = InsightType.DANGER
                    )
                )
            }
        }

        return insights
    }

    private fun getStartMillisForTimeframe(timeframe: TimeframeFilter): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        when (timeframe) {
            TimeframeFilter.DAILY -> {}
            TimeframeFilter.WEEKLY -> cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
            TimeframeFilter.MONTHLY -> cal.set(Calendar.DAY_OF_MONTH, 1)
            TimeframeFilter.YEARLY -> cal.set(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    private fun calculateBudgetForecast(txs: List<TransactionEntity>, limits: List<BudgetLimitEntity>): BudgetForecast {
        val cal = Calendar.getInstance()
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val daysElapsed = dayOfMonth.coerceAtLeast(1)
        val daysRemaining = (daysInMonth - daysElapsed).coerceAtLeast(0)

        val monthStartCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monthStartMillis = monthStartCal.timeInMillis

        val monthEndCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, daysInMonth)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val monthEndMillis = monthEndCal.timeInMillis

        val currentMonthTxs = txs.filter { it.dateMillis in monthStartMillis..monthEndMillis }
        val currentSpend = currentMonthTxs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val currentIncome = currentMonthTxs.filter { it.type == "INCOME" }.sumOf { it.amount }

        val monthlyLimits = limits.filter { it.periodType == "MONTHLY" || it.periodType.isEmpty() }
        val totalLimit = if (monthlyLimits.isNotEmpty()) monthlyLimits.sumOf { it.limitAmount } else 0.0

        val averageDailySpend = currentSpend / daysElapsed
        val projectedRemainingSpend = averageDailySpend * daysRemaining
        val projectedTotalExpense = currentSpend + projectedRemainingSpend

        val benchmarkLimit = if (totalLimit > 0) totalLimit else currentIncome
        val projectedRemainingBalance = if (benchmarkLimit > 0) (benchmarkLimit - projectedTotalExpense) else (currentIncome - projectedTotalExpense)

        val recommendedDailyAllowance = if (benchmarkLimit > currentSpend && daysRemaining > 0) {
            (benchmarkLimit - currentSpend) / daysRemaining
        } else 0.0

        val healthStatus = when {
            benchmarkLimit > 0 && projectedTotalExpense > benchmarkLimit -> ForecastHealth.OVER_BUDGET
            benchmarkLimit > 0 && projectedTotalExpense > (benchmarkLimit * 0.85) -> ForecastHealth.CAUTION
            else -> ForecastHealth.ON_TRACK
        }

        return BudgetForecast(
            currentSpend = currentSpend,
            currentIncome = currentIncome,
            totalBudgetLimit = totalLimit,
            daysElapsedInMonth = daysElapsed,
            daysInMonth = daysInMonth,
            daysRemaining = daysRemaining,
            averageDailySpend = averageDailySpend,
            projectedTotalExpense = projectedTotalExpense,
            projectedRemainingBalance = projectedRemainingBalance,
            recommendedDailyAllowance = recommendedDailyAllowance,
            healthStatus = healthStatus
        )
    }

    fun getMonthlyTrendsVsLimits(
        allTransactions: List<TransactionEntity>,
        limits: List<BudgetLimitEntity>,
        monthsCount: Int = 6
    ): List<MonthlyTrendVsLimitItem> {
        val expenses = allTransactions.filter { it.type == "EXPENSE" }
        val monthlyLimitObj = limits.find { it.periodType == "MONTHLY" && it.isEnabled }

        val explicitLimit = monthlyLimitObj?.limitAmount ?: 0.0
        val categoryMonthlyTotal = limits.filter { it.periodType == "CATEGORY" && it.isEnabled }.sumOf { it.limitAmount }
        val activeLimit = if (explicitLimit > 0) explicitLimit else if (categoryMonthlyTotal > 0) categoryMonthlyTotal else 1500.0

        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val fullFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

        val result = mutableListOf<MonthlyTrendVsLimitItem>()
        val nowCal = Calendar.getInstance()
        val currentMonthIdx = nowCal.get(Calendar.MONTH)
        val currentYearIdx = nowCal.get(Calendar.YEAR)

        for (m in (monthsCount - 1) downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.MONTH, -m)
            c.set(Calendar.DAY_OF_MONTH, 1)
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            val startMs = c.timeInMillis

            c.add(Calendar.MONTH, 1)
            val endMs = c.timeInMillis

            val monthSpend = expenses.filter { it.dateMillis in startMs until endMs }.sumOf { it.amount }

            c.timeInMillis = startMs
            val isCurrent = (c.get(Calendar.MONTH) == currentMonthIdx && c.get(Calendar.YEAR) == currentYearIdx)

            val monthLabel = monthFormat.format(Date(startMs))
            val fullMonthName = fullFormat.format(Date(startMs))

            result.add(
                MonthlyTrendVsLimitItem(
                    monthLabel = monthLabel,
                    fullMonthName = fullMonthName,
                    actualSpending = monthSpend,
                    setLimit = activeLimit,
                    isCurrentMonth = isCurrent
                )
            )
        }
        return result
    }
}
