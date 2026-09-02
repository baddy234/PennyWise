package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_templates")
data class ExpenseTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val amount: Double,
    val paymentMethod: String = "Credit Card",
    val note: String = "",
    val frequency: String = "MONTHLY", // "DAILY", "WEEKLY", "MONTHLY", "ONE_TIME"
    val isPlanned: Boolean = true,
    val iconName: String = "Receipt",
    val colorHex: String = "#818CF8"
)
