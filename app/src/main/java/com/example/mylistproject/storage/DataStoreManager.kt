package com.example.mylistproject.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.mylistproject.view.LembreteSerializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

private val Context.dataStore by preferencesDataStore(name = "lembretes")

class DataStoreManager(private val context: Context) {
    private val LEMBRETES_KEY = stringPreferencesKey("lembretes")

    suspend fun salvarLembretes(lista: List<LembreteSerializable>) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { prefs ->
                prefs[LEMBRETES_KEY] = Json.encodeToString(lista)
            }
        }
    }

    suspend fun carregarLembretes(): List<LembreteSerializable> {
        return withContext(Dispatchers.IO) {
            val prefs = context.dataStore.data.first()
            prefs[LEMBRETES_KEY]?.let {
                Json.decodeFromString(it)
            } ?: emptyList()
        }
    }
}
