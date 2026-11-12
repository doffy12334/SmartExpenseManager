package com.example.smartexpensemanager.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id : String,
    @SerialName("user_id")
    val userId : String,
    val name : String,
    val type : String,
    val icon : String? = null,
    val color : String? = null,
    @SerialName("created_at")
    val createdAt : String,
    @SerialName("updated_at")
    val updatedAt : String,
    @SerialName("deleted_at")
    val deletedAt : String? = null
) {
}