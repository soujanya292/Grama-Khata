package com.gramakhata.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("grama_khata_prefs", Context.MODE_PRIVATE)

    fun getShopName(): String = prefs.getString("shop_name", "My Shop") ?: "My Shop"
    fun setShopName(name: String) = prefs.edit().putString("shop_name", name).apply()

    fun getReminderTemplate(): String = prefs.getString("reminder_template", "Namaskara, your due at {shop_name} is Rs. {balance}") ?: "Namaskara, your due at {shop_name} is Rs. {balance}"
    fun setReminderTemplate(template: String) = prefs.edit().putString("reminder_template", template).apply()

    fun getUpiId(): String = prefs.getString("upi_id", "") ?: ""
    fun setUpiId(id: String) = prefs.edit().putString("upi_id", id).apply()

    fun getSettlementBufferDays(): Int = prefs.getInt("settlement_buffer", 3)
    fun setSettlementBufferDays(days: Int) = prefs.edit().putInt("settlement_buffer", days).apply()
}
