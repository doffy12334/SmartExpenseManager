package com.example.smartexpensemanager.data.remote.api

import com.example.smartexpensemanager.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.ktor.client.engine.okhttp.OkHttp
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {
    val supaClient : SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ){
            httpEngine = OkHttp.create()
            install(Auth){
                autoSaveToStorage = true
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
        }
    }
}