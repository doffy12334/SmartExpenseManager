package com.example.smartexpensemanager.data.remote.datasource

import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import com.example.smartexpensemanager.data.remote.dto.CategoryDto
import io.github.jan.supabase.postgrest.from

class CategoryRemoteDataSource {
    private val client = SupabaseProvider.supaClient
    suspend fun getCategories(userId : String) : List<CategoryDto>{
        return client.from("categories")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CategoryDto>()
    }
    suspend fun upsertCategory(category : CategoryDto){
         client.from("categories")
            .upsert(category)
    }
}