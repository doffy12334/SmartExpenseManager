package com.example.smartexpensemanager.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id : String,
    @SerialName("user_id")
    val userId : String,
    @SerialName("category_id")
    val categoryId : String? = null,
    val amount : Long,
    val type : String,
    val note : String? = null,
    @SerialName("transaction_date")
    val transactionDate : String,
    @SerialName("created_at")
    val createdAt : String,
    @SerialName("updated_at")
    val updatedAt : String,
    @SerialName("deleted_at")
    val deletedAt : String? = null
) {
}