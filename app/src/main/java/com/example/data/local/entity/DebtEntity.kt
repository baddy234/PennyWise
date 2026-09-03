package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personName: String,
    val type: String, // "OWED_TO_YOU" (You lent money / debtor) or "YOU_OWE" (You borrowed / creditor)
    val totalAmount: Double,
    val amountPaid: Double = 0.0,
    val dueDateMillis: Long? = null,
    val notes: String = "",
    val contactPhone: String = "",
    val isSettled: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
