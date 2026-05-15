package com.gramakhata.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

enum class TransactionType {
    CREDIT,   // Customer took goods (Balance increases)
    PAYMENT   // Customer paid money (Balance decreases)
}

enum class PaymentMethod {
    CASH,
    UPI
}

enum class UpiApp {
    NONE,
    GPAY,
    PHONEPE,
    PAYTM,
    OTHER
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class Transaction(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val customerId: String,
    val amount: Double,
    val type: TransactionType,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val upiApp: UpiApp = UpiApp.NONE,
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
