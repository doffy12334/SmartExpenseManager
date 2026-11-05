package com.example.smartexpensemanager.data.repository

import com.example.smartexpensemanager.data.remote.api.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.Phone

interface AuthRepository {
    suspend fun loginWithEmail(email:String, password: String) : Result<Unit>
    suspend fun loginWithPhone(phone: String, password: String) : Result<Unit>
    suspend fun registerWithEmail(email: String, password: String) : Result<Unit>
    suspend fun registerWithPhone(phone: String, password: String) : Result<Unit>
    suspend fun logout() : Result<Unit>
}

class AuthRepositoryImpl (
    private val provider : SupabaseProvider = SupabaseProvider
) : AuthRepository{

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            provider.supaClient.auth.signInWith(Email){
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun loginWithPhone(
        phone: String,
        password: String
    ): Result<Unit> {
        return runCatching{
            provider.supaClient.auth.signInWith(Phone){
                this.phone = phone
                this.password = password
            }
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            provider.supaClient.auth.signUpWith(Email){
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun registerWithPhone(
        phone: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            provider.supaClient.auth.signUpWith(Phone){
                this.phone = phone
                this.password = password
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching{
            provider.supaClient.auth.signOut()
        }
    }
}

