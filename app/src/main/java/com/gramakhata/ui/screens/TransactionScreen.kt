package com.gramakhata.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramakhata.data.entity.PaymentMethod
import com.gramakhata.data.entity.Transaction
import com.gramakhata.data.entity.TransactionType
import com.gramakhata.data.entity.UpiApp
import com.gramakhata.ui.viewmodel.TransactionViewModel
import com.gramakhata.util.CSVExporter
import com.gramakhata.util.IntentHelper
import com.gramakhata.util.PreferenceManager
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit
) {
    val customer by viewModel.customer.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showUpiDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(TransactionType.CREDIT) }
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = {
                        CSVExporter.exportTransactions(context, customer?.name ?: "Customer", transactions)
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Export Ledger")
                    }
                    IconButton(onClick = {
                        val msg = viewModel.getWhatsAppMessage()
                        IntentHelper.sendWhatsAppMessage(context, customer?.phone, msg)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Send Reminder")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Balance Header
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Balance", fontSize = 14.sp)
                    Text("₹$balance", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = if (balance > 0) Color(0xFFD32F2F) else Color(0xFF388E3C))
                }
            }

            // Quick Action Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { selectedType = TransactionType.CREDIT; showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Udari (+)")
                }
                Button(
                    onClick = { selectedType = TransactionType.PAYMENT; showAddDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                ) {
                    Text("Payment (-)")
                }
            }

            Button(
                onClick = { showUpiDialog = true },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.QrCode, null)
                Spacer(Modifier.width(8.dp))
                Text("Receive Online Payment")
            }

            // Transaction History
            Text("History", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            LazyColumn {
                items(transactions) { tx ->
                    TransactionItem(
                        tx = tx,
                        onDelete = { transactionToDelete = tx }
                    )
                    HorizontalDivider(thickness = 0.5.dp)
                }
            }
        }
    }

    if (showAddDialog) {
        AddTransactionDialog(
            type = selectedType,
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, method, upiApp, note ->
                viewModel.addTransaction(amount, selectedType, method, upiApp, note)
                showAddDialog = false
            }
        )
    }

    if (showUpiDialog) {
        val upiId = preferenceManager.getUpiId()
        if (upiId.isBlank()) {
            AlertDialog(
                onDismissRequest = { showUpiDialog = false },
                title = { Text("UPI ID Missing") },
                text = { Text("Please add your UPI ID in Settings to receive online payments.") },
                confirmButton = {
                    Button(onClick = { showUpiDialog = false }) { Text("OK") }
                }
            )
        } else {
            UPIDialog(
                upiId = upiId,
                amount = balance,
                shopName = preferenceManager.getShopName(),
                onDismiss = { showUpiDialog = false }
            )
        }
    }

    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Delete Transaction?") },
            text = { Text("Are you sure you want to delete this ₹${tx.amount} entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(tx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TransactionItem(tx: Transaction, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showMenu = true }
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (tx.type == TransactionType.CREDIT) "Udari" else "Payment", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.width(8.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(tx.paymentMethod.name)
                    }
                    if (tx.paymentMethod == PaymentMethod.UPI && tx.upiApp != UpiApp.NONE) {
                        Spacer(Modifier.width(4.dp))
                        Badge { Text(tx.upiApp.name) }
                    }
                }
                Text(date, fontSize = 12.sp, color = Color.Gray)
                tx.note?.let { 
                    Text("Reason: $it", fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
            Text(
                text = "₹${tx.amount}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (tx.type == TransactionType.CREDIT) Color(0xFFD32F2F) else Color(0xFF388E3C)
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    showMenu = false
                    onDelete()
                },
                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
            )
        }
    }
}

@Composable
fun AddTransactionDialog(type: TransactionType, onDismiss: () -> Unit, onConfirm: (Double, PaymentMethod, UpiApp, String?) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var upiApp by remember { mutableStateOf(UpiApp.NONE) }
    var error by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (type == TransactionType.CREDIT) "Give Udari (+)" else "Receive Payment (-)") },
        text = {
            Column {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = it
                        error = null
                    },
                    label = { Text("Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = note, 
                    onValueChange = { note = it }, 
                    label = { Text(if (type == TransactionType.CREDIT) "Reason (e.g. Rice, Sugar)" else "Note (Optional)") }, 
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text("Transfer Mode", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChip(
                        selected = method == PaymentMethod.CASH,
                        onClick = { method = PaymentMethod.CASH; upiApp = UpiApp.NONE },
                        label = { Text("Cash") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = method == PaymentMethod.UPI,
                        onClick = { method = PaymentMethod.UPI },
                        label = { Text("UPI") }
                    )
                }
                
                if (method == PaymentMethod.UPI) {
                    Spacer(Modifier.height(8.dp))
                    Text("Select App", style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        UpiApp.values().filter { it != UpiApp.NONE }.forEach { app ->
                            FilterChip(
                                selected = upiApp == app,
                                onClick = { upiApp = app },
                                label = { Text(app.name, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        error = "Please enter a valid amount"
                    } else {
                        onConfirm(amt, method, upiApp, note.takeIf { it.isNotBlank() })
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UPIDialog(upiId: String, amount: Double, shopName: String, onDismiss: () -> Unit) {
    val upiUri = "upi://pay?pa=$upiId&pn=${Uri.encode(shopName)}&am=$amount&cu=INR"
    val bitmap = remember(upiUri) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(upiUri, BarcodeFormat.QR_CODE, 512, 512)
        } catch (e: Exception) {
            null
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Scan to Pay Online") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "UPI QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Amount: ₹$amount", fontWeight = FontWeight.Bold)
                Text("Paying to: $shopName", style = MaterialTheme.typography.bodySmall)
                Text(upiId, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
