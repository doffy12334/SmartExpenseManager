package com.example.smartexpensemanager.data.remote.datasource

import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import com.example.smartexpensemanager.data.remote.dto.TransactionDto
import io.github.jan.supabase.postgrest.from

class TransactionRemoteDataSource {
    private val client = SupabaseProvider.supaClient
    suspend fun getTransactions(userId : String) : List<TransactionDto>{
        return client.from("transactions")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<TransactionDto>()
    }

    suspend fun upsertTransaction(transaction : TransactionDto){
        client.from("transactions")
            .upsert(transaction)
    }

}