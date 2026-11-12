package com.example.smartexpensemanager.data.remote.datasource

import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import com.example.smartexpensemanager.data.remote.dto.BudgetDto
import io.github.jan.supabase.postgrest.from

class BudgetRemoteDataSource {
    private val client = SupabaseProvider.supaClient
    suspend fun getBudget(userId : String) : List<BudgetDto>{
        return client.from("budgets")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<BudgetDto>()
    }
    suspend fun upsertBudget(budget : BudgetDto){
        client.from("budgets")
            .upsert(budget)
    }
}