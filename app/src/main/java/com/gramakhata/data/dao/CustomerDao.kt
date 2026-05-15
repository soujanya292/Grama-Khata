package com.gramakhata.data.dao

import androidx.room.*
import com.gramakhata.data.entity.Customer
import kotlinx.coroutines.flow.Flow

data class CustomerWithBalance(
    @Embedded val customer: Customer,
    val balance: Double
)

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("""
        SELECT c.*, 
        TOTAL(CASE WHEN t.type = 'CREDIT' THEN t.amount WHEN t.type = 'PAYMENT' THEN -t.amount ELSE 0 END) as balance
        FROM customers c 
        LEFT JOIN transactions t ON c.id = t.customerId
        GROUP BY c.id
        ORDER BY balance DESC
    """)
    fun getCustomersWithBalances(): Flow<List<CustomerWithBalance>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchCustomers(searchQuery: String): Flow<List<Customer>>
}
