package com.example.smartexpensemanager

import android.app.Application
import com.example.smartexpensemanager.data.remote.api.SupabaseProvider

class SmartExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseProvider.initialize(this)
    }
}