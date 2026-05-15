package com.gramakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gramakhata.ui.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    viewModel: CustomerViewModel,
    customerId: String?,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(customerId) {
        if (customerId != null) {
            viewModel.loadCustomer(customerId) { customer ->
                if (customer != null) {
                    name = customer.name
                    phone = customer.phone.orEmpty()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (customerId == null) "Add Customer" else "Edit Customer") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = if (it.isBlank()) "Name cannot be empty" else null
                },
                label = { Text("Customer Name") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { 
                    phone = it
                    phoneError = if (it.isNotEmpty() && it.length < 10) "Enter a valid phone number" else null
                },
                label = { Text("Phone Number (Optional)") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError != null,
                supportingText = { phoneError?.let { Text(it) } },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                    } else if (phoneError == null) {
                        viewModel.saveCustomer(customerId, name, phone.takeIf { it.isNotBlank() }, null, onBack)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                enabled = name.isNotBlank() && nameError == null && phoneError == null
            ) {
                Text("Save Customer")
            }
        }
    }
}
