package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FundEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FundDao {
    @Query("SELECT * FROM funds ORDER BY createdAt ASC")
    fun getAllFunds(): Flow<List<FundEntity>>

    @Query("SELECT * FROM funds ORDER BY createdAt ASC")
    suspend fun getAllFundsSnapshot(): List<FundEntity>

    @Query("SELECT * FROM funds WHERE id = :id")
    suspend fun getFundById(id: Long): FundEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFund(fund: FundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunds(funds: List<FundEntity>)

    @Update
    suspend fun updateFund(fund: FundEntity)

    @Delete
    suspend fun deleteFund(fund: FundEntity)

    @Query("DELETE FROM funds WHERE id = :id")
    suspend fun deleteFundById(id: Long)

    @Query("UPDATE funds SET balance = balance + :delta WHERE id = :fundId")
    suspend fun adjustFundBalance(fundId: Long, delta: Double)

    @Query("UPDATE funds SET balance = :newBalance WHERE id = :fundId")
    suspend fun setFundBalance(fundId: Long, newBalance: Double)

    @Query("DELETE FROM funds")
    suspend fun deleteAllFunds()
}
