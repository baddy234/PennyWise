package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BudgetPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    CATEGORY
}

@Entity(tableName = "budget_limits")
data class BudgetLimitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val periodType: String, // "DAILY", "WEEKLY", "MONTHLY", "YEARLY", "CATEGORY"
    val categoryName: String? = null,
    val limitAmount: Double,
    val isEnabled: Boolean = true,
    val notifyThresholdPercent: Int = 80 // Alert at 80% or 100%
)
