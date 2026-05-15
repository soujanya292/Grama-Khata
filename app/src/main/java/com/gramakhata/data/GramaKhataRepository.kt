package com.gramakhata.data

import com.gramakhata.data.dao.CustomerDao
import com.gramakhata.data.dao.CustomerWithBalance
import com.gramakhata.data.dao.TransactionDao
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

class GramaKhataRepository(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    // Customer operations
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    fun getCustomersWithBalances(): Flow<List<CustomerWithBalance>> = customerDao.getCustomersWithBalances()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)
    suspend fun getCustomerById(id: String): Customer? = customerDao.getCustomerById(id)
    suspend fun insertCustomer(customer: Customer) = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    // Transaction operations
    fun getTransactionsForCustomer(customerId: String): Flow<List<Transaction>> = 
        transactionDao.getTransactionsForCustomer(customerId)
    
    fun getDailyTransactions(startOfDay: Long): Flow<List<Transaction>> = 
        transactionDao.getDailyTransactions(startOfDay)

    suspend fun insertTransaction(transaction: Transaction) = transactionDao.insertTransaction(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.deleteTransaction(transaction)
}
