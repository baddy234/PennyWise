package com.example.data.repository

import com.example.data.local.dao.BudgetLimitDao
import com.example.data.local.dao.ExpenseTemplateDao
import com.example.data.local.dao.FundDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.ExpenseTemplateEntity
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class BudgetRepository(
    private val transactionDao: TransactionDao,
    private val budgetLimitDao: BudgetLimitDao,
    private val fundDao: FundDao,
    private val expenseTemplateDao: ExpenseTemplateDao,
    private val notificationHelper: NotificationHelper
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allLimits: Flow<List<BudgetLimitEntity>> = budgetLimitDao.getAllLimits()
    val allFunds: Flow<List<FundEntity>> = fundDao.getAllFunds()
    val allTemplates: Flow<List<ExpenseTemplateEntity>> = expenseTemplateDao.getAllTemplates()

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        val id = transactionDao.insertTransaction(transaction)
        
        // Subtract or add from linked fund if specified
        transaction.fundId?.let { fId ->
            val delta = if (transaction.type == "EXPENSE") -transaction.amount else transaction.amount
            fundDao.adjustFundBalance(fId, delta)
        }

        // Check if any limits were breached by this new expense
        if (transaction.type == "EXPENSE") {
            checkLimitsAndNotify()
        }
        return id
    }

    suspend fun updateTransaction(oldTransaction: TransactionEntity?, newTransaction: TransactionEntity) {
        // Revert old transaction's fund balance impact if applicable
        if (oldTransaction != null && oldTransaction.fundId != null) {
            val revertDelta = if (oldTransaction.type == "EXPENSE") oldTransaction.amount else -oldTransaction.amount
            fundDao.adjustFundBalance(oldTransaction.fundId, revertDelta)
        }

        transactionDao.updateTransaction(newTransaction)

        // Apply new transaction's fund balance impact if applicable
        if (newTransaction.fundId != null) {
            val applyDelta = if (newTransaction.type == "EXPENSE") -newTransaction.amount else newTransaction.amount
            fundDao.adjustFundBalance(newTransaction.fundId, applyDelta)
        }

        if (newTransaction.type == "EXPENSE") {
            checkLimitsAndNotify()
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
        // Revert fund impact on deletion
        transaction.fundId?.let { fId ->
            val revertDelta = if (transaction.type == "EXPENSE") transaction.amount else -transaction.amount
            fundDao.adjustFundBalance(fId, revertDelta)
        }
    }

    suspend fun deleteTransactionById(id: Long) {
        val tx = transactionDao.getTransactionById(id)
        if (tx != null) {
            deleteTransaction(tx)
        } else {
            transactionDao.deleteTransactionById(id)
        }
    }

    // Funds CRUD
    suspend fun insertFund(fund: FundEntity): Long {
        return fundDao.insertFund(fund)
    }

    suspend fun updateFund(fund: FundEntity) {
        fundDao.updateFund(fund)
    }

    suspend fun deleteFund(fund: FundEntity) {
        fundDao.deleteFund(fund)
    }

    // Expense Templates CRUD
    suspend fun insertTemplate(template: ExpenseTemplateEntity): Long {
        return expenseTemplateDao.insertTemplate(template)
    }

    suspend fun updateTemplate(template: ExpenseTemplateEntity) {
        expenseTemplateDao.updateTemplate(template)
    }

    suspend fun deleteTemplate(template: ExpenseTemplateEntity) {
        expenseTemplateDao.deleteTemplate(template)
    }

    suspend fun adjustFundBalance(fundId: Long, delta: Double) {
        fundDao.adjustFundBalance(fundId, delta)
    }

    suspend fun setFundBalance(fundId: Long, newBalance: Double) {
        fundDao.setFundBalance(fundId, newBalance)
    }

    suspend fun getAllFundsSnapshot(): List<FundEntity> {
        return fundDao.getAllFundsSnapshot()
    }

    suspend fun insertOrUpdateLimit(limit: BudgetLimitEntity): Long {
        val id = budgetLimitDao.insertLimit(limit)
        checkLimitsAndNotify()
        return id
    }

    suspend fun deleteLimit(limit: BudgetLimitEntity) {
        budgetLimitDao.deleteLimit(limit)
    }

    suspend fun getAllTransactionsSnapshot(): List<TransactionEntity> {
        return transactionDao.getAllTransactionsList()
    }

    suspend fun getAllLimitsSnapshot(): List<BudgetLimitEntity> {
        return budgetLimitDao.getAllLimitsList()
    }

    suspend fun restoreDatabase(transactions: List<TransactionEntity>, limits: List<BudgetLimitEntity>) {
        transactionDao.deleteAllTransactions()
        budgetLimitDao.deleteAllLimits()
        transactionDao.insertTransactions(transactions)
        budgetLimitDao.insertLimits(limits)
        checkLimitsAndNotify()
    }

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
    }

    suspend fun checkLimitsAndNotify() {
        val transactions = transactionDao.getAllTransactionsList().filter { it.type == "EXPENSE" }
        val limits = budgetLimitDao.getAllLimitsList().filter { it.isEnabled }

        val cal = Calendar.getInstance()
        val now = cal.timeInMillis

        // Calculate Daily Spend (Today from 00:00:00)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfToday = cal.timeInMillis

        // Calculate Weekly Spend (Start of current week)
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val startOfWeek = cal.timeInMillis

        // Calculate Monthly Spend (Start of current month)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = cal.timeInMillis

        // Calculate Yearly Spend (Start of current year)
        cal.set(Calendar.DAY_OF_YEAR, 1)
        val startOfYear = cal.timeInMillis

        val dailySpend = transactions.filter { it.dateMillis >= startOfToday }.sumOf { it.amount }
        val weeklySpend = transactions.filter { it.dateMillis >= startOfWeek }.sumOf { it.amount }
        val monthlySpend = transactions.filter { it.dateMillis >= startOfMonth }.sumOf { it.amount }
        val yearlySpend = transactions.filter { it.dateMillis >= startOfYear }.sumOf { it.amount }

        for (limit in limits) {
            val (spent, periodLabel) = when (limit.periodType) {
                "DAILY" -> Pair(dailySpend, "Daily")
                "WEEKLY" -> Pair(weeklySpend, "Weekly")
                "MONTHLY" -> Pair(monthlySpend, "Monthly")
                "YEARLY" -> Pair(yearlySpend, "Yearly")
                "CATEGORY" -> {
                    val catName = limit.categoryName ?: continue
                    val catSpend = transactions
                        .filter { it.dateMillis >= startOfMonth && it.category.equals(catName, ignoreCase = true) }
                        .sumOf { it.amount }
                    Pair(catSpend, "$catName (Monthly)")
                }
                else -> Pair(0.0, "")
            }

            if (periodLabel.isEmpty() || limit.limitAmount <= 0) continue

            val ratio = spent / limit.limitAmount
            val thresholdRatio = limit.notifyThresholdPercent / 100.0

            if (ratio >= 1.0) {
                // Exceeded limit!
                val excess = spent - limit.limitAmount
                notificationHelper.sendBudgetAlert(
                    title = "⚠️ $periodLabel Budget Exceeded!",
                    message = "You have spent $${String.format("%.2f", spent)} against your limit of $${String.format("%.2f", limit.limitAmount)} (over by $${String.format("%.2f", excess)}).",
                    isWarningOnly = false,
                    notificationId = limit.id.toInt() + 100
                )
            } else if (ratio >= thresholdRatio) {
                // Warning threshold reached!
                val percentInt = (ratio * 100).toInt()
                notificationHelper.sendBudgetAlert(
                    title = "⚡ $periodLabel Budget Warning ($percentInt%)",
                    message = "You've reached $percentInt% of your $periodLabel limit ($${String.format("%.2f", spent)} / $${String.format("%.2f", limit.limitAmount)}).",
                    isWarningOnly = true,
                    notificationId = limit.id.toInt() + 200
                )
            }
        }
    }
}
