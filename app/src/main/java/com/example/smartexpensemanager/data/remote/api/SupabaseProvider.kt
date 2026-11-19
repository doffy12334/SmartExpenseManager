package com.example.smartexpensemanager.data.remote.api

import android.content.Context
import com.example.smartexpensemanager.BuildConfig
import com.example.smartexpensemanager.data.local.auth.DatastoreSessionManager
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.ktor.client.engine.okhttp.OkHttp
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseProvider {

    @Volatile
    private var appContext : Context? = null
    @Synchronized
    fun initialize(context : Context){
        if (appContext == null){
            appContext = context.applicationContext
        }
    }


    val supaClient : SupabaseClient by lazy {
        val context = checkNotNull(appContext){
            "Cần gọi SupabaseProvider.initialize() trước khi dùng supaClient."
        }
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ){
            httpEngine = OkHttp.create()
            install(Auth){
                sessionManager = DatastoreSessionManager(context)
                autoSaveToStorage = true
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
        }
    }
}