package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ExpenseTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseTemplateDao {
    @Query("SELECT * FROM expense_templates ORDER BY id DESC")
    fun getAllTemplates(): Flow<List<ExpenseTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: ExpenseTemplateEntity): Long

    @Update
    suspend fun updateTemplate(template: ExpenseTemplateEntity)

    @Delete
    suspend fun deleteTemplate(template: ExpenseTemplateEntity)

    @Query("DELETE FROM expense_templates")
    suspend fun deleteAllTemplates()
}
