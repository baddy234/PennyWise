package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class FundIconOption(
    val name: String,
    val label: String,
    val icon: ImageVector
)

data class FundColorOption(
    val hex: String,
    val label: String,
    val color: Color
)

object FundVisualHelper {
    val iconOptions = listOf(
        FundIconOption("Wallet", "Wallet", Icons.Default.AccountBalanceWallet),
        FundIconOption("Bank", "Bank Checking", Icons.Default.AccountBalance),
        FundIconOption("Savings", "Piggy Bank", Icons.Default.Savings),
        FundIconOption("Cash", "Cash Stash", Icons.Default.LocalAtm),
        FundIconOption("Card", "Debit / Credit", Icons.Default.CreditCard),
        FundIconOption("Investment", "Investments", Icons.Default.TrendingUp),
        FundIconOption("Travel", "Travel / Vacation", Icons.Default.Flight),
        FundIconOption("Emergency", "Emergency / Health", Icons.Default.Favorite),
        FundIconOption("Goal", "Goal / Dream", Icons.Default.EmojiEvents),
        FundIconOption("Home", "Mortgage / House", Icons.Default.Home)
    )

    val colorOptions = listOf(
        FundColorOption("#6366F1", "Indigo", Color(0xFF6366F1)),
        FundColorOption("#38BDF8", "Cyan", Color(0xFF38BDF8)),
        FundColorOption("#34D399", "Emerald", Color(0xFF34D399)),
        FundColorOption("#FBBF24", "Amber", Color(0xFFFBBF24)),
        FundColorOption("#FB7185", "Rose", Color(0xFFFB7185)),
        FundColorOption("#A855F7", "Purple", Color(0xFFA855F7)),
        FundColorOption("#EC4899", "Pink", Color(0xFFEC4899)),
        FundColorOption("#14B8A6", "Teal", Color(0xFF14B8A6))
    )

    fun getIcon(iconName: String): ImageVector {
        return iconOptions.find { it.name.equals(iconName, ignoreCase = true) }?.icon
            ?: Icons.Default.AccountBalanceWallet
    }

    fun getColor(colorHex: String): Color {
        return try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF6366F1)
        }
    }
}
