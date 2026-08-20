package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpenseCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val isIncome: Boolean = false
)

object CategoryRegistry {
    val defaultCategories = listOf(
        ExpenseCategory("food", "Food & Dining", Icons.Default.Restaurant, Color(0xFF38BDF8)),
        ExpenseCategory("groceries", "Groceries", Icons.Default.ShoppingCart, Color(0xFFFB7185)),
        ExpenseCategory("shopping", "Shopping", Icons.Default.ShoppingBag, Color(0xFF818CF8)),
        ExpenseCategory("housing", "Housing & Rent", Icons.Default.Home, Color(0xFF34D399)),
        ExpenseCategory("transport", "Transport", Icons.Default.DirectionsCar, Color(0xFFFBBF24)),
        ExpenseCategory("entertainment", "Entertainment", Icons.Default.Movie, Color(0xFFF472B6)),
        ExpenseCategory("health", "Health & Fitness", Icons.Default.FitnessCenter, Color(0xFFA78BFA)),
        ExpenseCategory("utilities", "Utilities & Bills", Icons.Default.Receipt, Color(0xFF2DD4BF)),
        ExpenseCategory("education", "Education", Icons.Default.School, Color(0xFF60A5FA)),
        ExpenseCategory("income", "Salary & Income", Icons.Default.LocalAtm, Color(0xFF4ADE80), isIncome = true),
        ExpenseCategory("investment", "Investments", Icons.Default.TrendingUp, Color(0xFFE879F9), isIncome = true),
        ExpenseCategory("other", "Other", Icons.Default.AccountBalance, Color(0xFF94A3B8))
    )

    fun getCategory(name: String): ExpenseCategory {
        return defaultCategories.find { it.name.equals(name, ignoreCase = true) || it.id.equals(name, ignoreCase = true) }
            ?: ExpenseCategory("other", name, Icons.Default.AccountBalance, Color(0xFF94A3B8))
    }
}
