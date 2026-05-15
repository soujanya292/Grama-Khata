package com.gramakhata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakhata.data.GramaKhataRepository
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.Transaction
import com.gramakhata.data.entity.TransactionType
import com.gramakhata.util.PreferenceManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: GramaKhataRepository,
    private val customerId: String,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val customer = flow {
        emit(repository.getCustomerById(customerId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions = repository.getTransactionsForCustomer(customerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val balance = transactions.map { list ->
        list.sumOf { if (it.type == TransactionType.CREDIT) it.amount else -it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addTransaction(amount: Double, type: TransactionType, method: com.gramakhata.data.entity.PaymentMethod, upiApp: com.gramakhata.data.entity.UpiApp, note: String?) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = type,
                    paymentMethod = method,
                    upiApp = upiApp,
                    note = note
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun getWhatsAppMessage(): String {
        val shopName = preferenceManager.getShopName()
        val template = preferenceManager.getReminderTemplate()
        val bufferDays = preferenceManager.getSettlementBufferDays()
        
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, bufferDays)
        val settlementDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(calendar.time)
        
        return template
            .replace("{shop_name}", shopName)
            .replace("{balance}", "₹${balance.value}")
            .replace("{date}", settlementDate)
    }
}
