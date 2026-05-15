package com.gramakhata.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gramakhata.util.PreferenceManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    preferenceManager: PreferenceManager,
    onBack: () -> Unit
) {
    var shopName by remember { mutableStateOf(preferenceManager.getShopName()) }
    var upiId by remember { mutableStateOf(preferenceManager.getUpiId()) }
    var reminderTemplate by remember { mutableStateOf(preferenceManager.getReminderTemplate()) }
    var bufferDays by remember { mutableStateOf(preferenceManager.getSettlementBufferDays()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Profile & Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text("UPI ID (for receiving payments)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. yourname@upi") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Settlement Buffer (Reminders)", style = MaterialTheme.typography.labelMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 3, 7).forEach { days ->
                    FilterChip(
                        selected = bufferDays == days,
                        onClick = { bufferDays = days },
                        label = { Text(if (days == 1) "1 Day" else "$days Days") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = reminderTemplate,
                onValueChange = { reminderTemplate = it },
                label = { Text("Reminder Message Template") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Use {shop_name}, {balance}, and {date} as placeholders") }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    preferenceManager.setShopName(shopName)
                    preferenceManager.setUpiId(upiId)
                    preferenceManager.setReminderTemplate(reminderTemplate)
                    preferenceManager.setSettlementBufferDays(bufferDays)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
        }
    }
}
