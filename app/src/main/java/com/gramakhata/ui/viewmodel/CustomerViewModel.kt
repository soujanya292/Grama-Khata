package com.gramakhata.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramakhata.data.GramaKhataRepository
import com.gramakhata.data.entity.Customer
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: GramaKhataRepository) : ViewModel() {

    fun loadCustomer(id: String, onLoaded: (Customer?) -> Unit) {
        viewModelScope.launch {
            onLoaded(repository.getCustomerById(id))
        }
    }

    fun saveCustomer(id: String?, name: String, phone: String?, photoUri: String?, onComplete: () -> Unit) {
        viewModelScope.launch {
            val customer = if (id != null) {
                val existingCustomer = repository.getCustomerById(id)
                Customer(
                    id = id,
                    name = name,
                    phone = phone,
                    photoUri = photoUri,
                    createdAt = existingCustomer?.createdAt ?: System.currentTimeMillis()
                )
            } else {
                Customer(name = name, phone = phone, photoUri = photoUri)
            }
            
            if (id != null) {
                repository.updateCustomer(customer)
            } else {
                repository.insertCustomer(customer)
            }
            onComplete()
        }
    }

    fun deleteCustomer(customer: Customer, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            onComplete()
        }
    }
}
