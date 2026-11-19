package com.example.smartexpensemanager.data.local.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import java.security.GeneralSecurityException


private val Context.authSessionDataStore by preferencesDataStore(
    name = "auth_session"
)

internal class DatastoreSessionManager(context: Context) : SessionManager {
    private val dataStore = context.applicationContext.authSessionDataStore
    private val sessionKey = stringPreferencesKey("encrypted-session")
    private val mutex = Mutex()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun saveSession(session: UserSession) {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val sessionJson = json.encodeToString(session)
                val encryptSession = SessionCipher.encrypt(sessionJson)
                dataStore.edit { preferences ->
                    preferences[sessionKey] = encryptSession
                }
            }
        }

    }

    override suspend fun loadSession(): UserSession? {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val encryptedSession = dataStore.data.first()[sessionKey]
                    ?: return@withLock null
                val session = decodeSession(encryptedSession)
                if (session == null) {
                    dataStore.edit { preferences ->
                        preferences.remove(sessionKey)
                    }
                }
                session
            }

        }
    }

    override suspend fun deleteSession() {
        withContext(Dispatchers.IO) {
            mutex.withLock {
                dataStore.edit { preferences ->
                    preferences.remove(sessionKey)
                }
            }
        }
    }

    private fun decodeSession(encryptSession : String) : UserSession?{
            return try{
                val sessionJson = SessionCipher.decrypt(encryptSession)
                    ?: return null
                json.decodeFromString(sessionJson)
            }catch (_: GeneralSecurityException ){
                //vd:Khoa khong duoc su dung hoac ban ma bi thay doi
                null
            }catch (_:SerializationException){
                //vd:Json khong hop le hoac khong doc duoc UserSession
                null
            }catch(_: IllegalArgumentException){
                // vd: Chuoi Base64 khong hop le
                null
            }
    }

}
