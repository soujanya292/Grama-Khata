package com.gramakhata.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramakhata.R
import com.gramakhata.data.dao.CustomerWithBalance
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.PaymentMethod
import com.gramakhata.data.entity.TransactionType
import com.gramakhata.data.entity.UpiApp
import com.gramakhata.ui.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onCustomerClick: (String) -> Unit,
    onAddCustomerClick: () -> Unit,
    onEditCustomerClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onReportClick: () -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val summary by viewModel.financialSummary.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }
    var quickAddTransactionCustomer by remember { mutableStateOf<Customer?>(null) }
    
    val dailyReport by viewModel.dailyReport.collectAsState()
    val monthlyReport by viewModel.monthlyReport.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Grama Katha", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { showReportDialog = true }) {
                        Icon(Icons.Default.Summarize, contentDescription = "Summary Reports", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomerClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // Financial Summary Chart Card
            FinancialSummaryCard(summary)

            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                label = { Text("Search customer") },
                placeholder = { Text("Enter customer name") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            if (customers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_empty_dashboard),
                            contentDescription = null,
                            modifier = Modifier.size(200.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isBlank()) {
                                "No customers yet. Tap + to add your first customer."
                            } else {
                                "No customers match \"$searchQuery\"."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(customers, key = { it.customer.id }) { customer ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn()
                        ) {
                            CustomerItem(
                                item = customer,
                                onClick = { onCustomerClick(customer.customer.id) },
                                onEdit = { onEditCustomerClick(customer.customer.id) },
                                onDelete = { customerToDelete = customer.customer },
                                onQuickAdd = { quickAddTransactionCustomer = customer.customer }
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Business Summary") },
            text = {
                Column {
                    Text(dailyReport)
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(16.dp))
                    Text(monthlyReport)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReportClick()
                        showReportDialog = false
                    }
                ) {
                    Text("Close")
                }
            }
        )
    }

    quickAddTransactionCustomer?.let { customer ->
        AddTransactionQuickDialog(
            customerName = customer.name,
            onDismiss = { quickAddTransactionCustomer = null },
            onConfirm = { amount, type, method, upiApp, note ->
                viewModel.addQuickTransaction(customer.id, amount, type, method, upiApp, note)
                quickAddTransactionCustomer = null
            }
        )
    }

    customerToDelete?.let { customer ->
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Delete Customer?") },
            text = { Text("Are you sure you want to delete ${customer.name}? This will remove all their transactions.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCustomer(customer)
                        customerToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FinancialSummaryCard(summary: DashboardViewModel.FinancialSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Business Status", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                SummaryRow("Total Outstanding", "₹${summary.totalCredit}", Color(0xFFD32F2F))
                SummaryRow("Total Collected", "₹${summary.totalPayment}", Color(0xFF388E3C))
                Spacer(Modifier.height(4.dp))
                Text("${summary.pendingCustomers} Pending | ${summary.settledCustomers} Settled", style = MaterialTheme.typography.labelSmall)
            }
            
            SimplePieChart(
                credit = summary.totalCredit.toFloat(),
                payment = summary.totalPayment.toFloat(),
                modifier = Modifier.size(70.dp)
            )
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun SimplePieChart(credit: Float, payment: Float, modifier: Modifier = Modifier) {
    val total = credit + payment
    val creditAngle = if (total > 0) (credit / total) * 360f else 180f
    val paymentAngle = if (total > 0) (payment / total) * 360f else 180f

    Canvas(modifier = modifier) {
        drawArc(
            color = Color(0xFFD32F2F),
            startAngle = -90f,
            sweepAngle = creditAngle,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
        drawArc(
            color = Color(0xFF388E3C),
            startAngle = -90f + creditAngle,
            sweepAngle = paymentAngle,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CustomerItem(
    item: CustomerWithBalance,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickAdd: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { showMenu = true }
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.customer.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(item.customer.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        item.customer.phone?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${item.balance}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (item.balance > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        )
                        Text(
                            text = if (item.balance > 0) "Pending" else "Settled",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.balance > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onQuickAdd) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Quick Add", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Edit") },
                    onClick = {
                        showMenu = false
                        onEdit()
                    },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
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
}

@Composable
fun AddTransactionQuickDialog(customerName: String, onDismiss: () -> Unit, onConfirm: (Double, TransactionType, PaymentMethod, UpiApp, String?) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.CREDIT) }
    var note by remember { mutableStateOf("") }
    var method by remember { mutableStateOf(PaymentMethod.CASH) }
    var upiApp by remember { mutableStateOf(UpiApp.NONE) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quick Entry for $customerName") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(
                        selected = type == TransactionType.CREDIT,
                        onClick = { type = TransactionType.CREDIT },
                        label = { Text("Udari (+)") }
                    )
                    FilterChip(
                        selected = type == TransactionType.PAYMENT,
                        onClick = { type = TransactionType.PAYMENT },
                        label = { Text("Payment (-)") }
                    )
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Enter Amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Purpose / Note") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
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
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        UpiApp.entries.filter { it != UpiApp.NONE }.forEach { app ->
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
                    amount.toDoubleOrNull()?.let { onConfirm(it, type, method, upiApp, note.takeIf { it.isNotBlank() }) }
                },
                enabled = amount.isNotBlank()
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
