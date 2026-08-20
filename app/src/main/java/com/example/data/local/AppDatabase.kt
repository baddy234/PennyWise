package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BudgetLimitDao
import com.example.data.local.dao.FundDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.entity.BudgetLimitEntity
import com.example.data.local.entity.FundEntity
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope

@Database(
    entities = [TransactionEntity::class, BudgetLimitEntity::class, FundEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetLimitDao(): BudgetLimitDao
    abstract fun fundDao(): FundDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope? = null): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_lens_app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

