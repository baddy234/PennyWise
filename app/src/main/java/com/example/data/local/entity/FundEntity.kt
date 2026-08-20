package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funds")
data class FundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val balance: Double = 0.0,
    val targetAmount: Double? = null,
    val colorHex: String = "#6366F1", // Hex color string e.g. #38BDF8, #34D399, etc.
    val iconName: String = "Wallet", // "Wallet", "Savings", "Bank", "Cash", "Investment", "Travel", "Emergency", "Card"
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
