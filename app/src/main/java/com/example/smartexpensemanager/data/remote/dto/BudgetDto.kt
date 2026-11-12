package com.example.smartexpensemanager.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BudgetDto(
    val id : String,
    @SerialName("user_id")
    val userId : String,
    @SerialName("category_id")
    val categoryId : String? = null,
    @SerialName("amount_limit")
    val amountLimit : Long,
    val month : String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
    @SerialName("deleted_at")
    val deletedAt: String? = null
) {
}