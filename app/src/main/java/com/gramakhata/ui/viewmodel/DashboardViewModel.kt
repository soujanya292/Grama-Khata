package com.gramakhata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakhata.data.GramaKhataRepository
import com.gramakhata.data.dao.CustomerWithBalance
import com.gramakhata.data.entity.Customer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar

class DashboardViewModel(private val repository: GramaKhataRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Main list of customers with their balances, updated in real-time
    val customers = combine(
        repository.getCustomersWithBalances(),
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { it.customer.name.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    data class FinancialSummary(
        val totalCredit: Double,
        val totalPayment: Double,
        val settledCustomers: Int,
        val pendingCustomers: Int
    )

    val financialSummary: StateFlow<FinancialSummary> = repository.getCustomersWithBalances().map { list ->
        val credit = list.filter { it.balance > 0 }.sumOf { it.balance }
        val payment = list.filter { it.balance < 0 }.sumOf { kotlin.math.abs(it.balance) }
        FinancialSummary(
            totalCredit = credit,
            totalPayment = payment,
            settledCustomers = list.count { it.balance == 0.0 },
            pendingCustomers = list.count { it.balance != 0.0 }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary(0.0, 0.0, 0, 0))

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }

    fun addQuickTransaction(customerId: String, amount: Double, type: com.gramakhata.data.entity.TransactionType, method: com.gramakhata.data.entity.PaymentMethod, upiApp: com.gramakhata.data.entity.UpiApp, note: String?) {
        viewModelScope.launch {
            repository.insertTransaction(
                com.gramakhata.data.entity.Transaction(
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

    // Logic for the Daily Report summary
    val dailyReport: StateFlow<String> = flow {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        repository.getDailyTransactions(startOfDay).collect { transactions ->
            val sales = transactions.filter { it.type.name == "CREDIT" }.sumOf { it.amount }
            val collections = transactions.filter { it.type.name == "PAYMENT" }.sumOf { it.amount }
            val netDues = sales - collections
            emit("""
                Today's Business Summary
                ------------------------
                Today you sold for: ₹$sales
                Today's collections: ₹$collections
                ------------------------
                Net dues added today: ₹$netDues
                Total Entries: ${transactions.size}
                """.trimIndent())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Loading...")

    // Logic for the Monthly Report summary
    val monthlyReport: StateFlow<String> = flow {
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        repository.getDailyTransactions(startOfMonth).collect { transactions ->
            val credits = transactions.filter { it.type.name == "CREDIT" }.sumOf { it.amount }
            val payments = transactions.filter { it.type.name == "PAYMENT" }.sumOf { it.amount }
            val monthName = DateFormat.getDateInstance(DateFormat.LONG).format(startOfMonth)
            emit("""
                Monthly Summary (From $monthName)
                ------------------------
                Total Credit: Rs. $credits
                Total Payment: Rs. $payments
                Net Collection: Rs. ${payments - credits}
                ------------------------
                Total Entries: ${transactions.size}
                """.trimIndent())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Loading...")
}
