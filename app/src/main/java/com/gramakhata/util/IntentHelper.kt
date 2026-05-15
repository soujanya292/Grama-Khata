package com.gramakhata.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object IntentHelper {
    fun sendWhatsAppMessage(context: Context, phone: String?, message: String) {
        try {
            val packageManager = context.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            
            // If phone exists, target that specific number
            val url = if (!phone.isNullOrBlank()) {
                "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
            } else {
                "https://api.whatsapp.com/send?text=${Uri.encode(message)}"
            }
            
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            // Fallback to generic share
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
    }
    
    fun sendSms(context: Context, phone: String?, message: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${phone ?: ""}"))
        intent.putExtra("sms_body", message)
        context.startActivity(intent)
    }
}
