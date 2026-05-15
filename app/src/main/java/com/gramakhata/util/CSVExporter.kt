package com.gramakhata.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.gramakhata.data.entity.Transaction
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CSVExporter {
    fun exportTransactions(context: Context, customerName: String, transactions: List<Transaction>) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName = "Ledger_${customerName.replace(" ", "_")}_${System.currentTimeMillis()}.csv"
            val file = File(context.cacheDir, fileName)
            
            try {
                file.printWriter().use { writer ->
                    writer.println("Date,Type,Amount,Note")
                    
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    
                    transactions.forEach { tx ->
                        writer.println("${dateFormat.format(Date(tx.timestamp))},${tx.type},${tx.amount},${tx.note ?: ""}")
                    }
                }
                
                withContext(Dispatchers.Main) {
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_SUBJECT, "Ledger for $customerName")
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    
                    context.startActivity(Intent.createChooser(intent, "Export Ledger"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
