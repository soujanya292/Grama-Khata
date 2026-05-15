package com.gramakhata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String? = null,
    val photoUri: String? = null, // Store image location locally
    val createdAt: Long = System.currentTimeMillis()
)
