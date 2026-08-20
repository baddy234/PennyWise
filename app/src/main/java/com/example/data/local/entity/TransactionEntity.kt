package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: String = "EXPENSE", // "EXPENSE" or "INCOME"
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMethod: String = "Card", // "Card", "Cash", "Bank Transfer", "Crypto", "Other"
    val fundId: Long? = null,
    val fundName: String? = null,
    val isRecurring: Boolean = false,
    val recurringInterval: String = "MONTHLY", // "WEEKLY", "MONTHLY", "YEARLY"
    val nextDueDateMillis: Long? = null
)
