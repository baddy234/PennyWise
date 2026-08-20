package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.BudgetLimitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetLimitDao {
    @Query("SELECT * FROM budget_limits")
    fun getAllLimits(): Flow<List<BudgetLimitEntity>>

    @Query("SELECT * FROM budget_limits WHERE periodType = :periodType LIMIT 1")
    suspend fun getLimitByPeriod(periodType: String): BudgetLimitEntity?

    @Query("SELECT * FROM budget_limits WHERE periodType = 'CATEGORY' AND categoryName = :categoryName LIMIT 1")
    suspend fun getLimitByCategory(categoryName: String): BudgetLimitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimit(limit: BudgetLimitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLimits(limits: List<BudgetLimitEntity>)

    @Update
    suspend fun updateLimit(limit: BudgetLimitEntity)

    @Delete
    suspend fun deleteLimit(limit: BudgetLimitEntity)

    @Query("DELETE FROM budget_limits")
    suspend fun deleteAllLimits()

    @Query("SELECT * FROM budget_limits")
    suspend fun getAllLimitsList(): List<BudgetLimitEntity>
}
