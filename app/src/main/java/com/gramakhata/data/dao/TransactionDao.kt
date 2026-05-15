package com.gramakhata.data.dao

import androidx.room.*
import com.gramakhata.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getDailyTransactions(startOfDay: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsForCustomer(customerId: String)
}
