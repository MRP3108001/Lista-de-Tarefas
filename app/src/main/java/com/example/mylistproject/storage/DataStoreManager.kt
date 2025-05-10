package com.example.mylistproject.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.mylistproject.view.LembreteSerializable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

private val Context.dataStore by preferencesDataStore(name = "mylist_store")

class DataStoreManager(private val context: Context) {

    private val USUARIOS_KEY: Preferences.Key<String> = stringPreferencesKey("usuarios")
    private val USUARIO_LOGADO_KEY: Preferences.Key<String> = stringPreferencesKey("usuario_logado")

    suspend fun registrarUsuario(usuario: String, senha: String): Boolean {
        return withContext(Dispatchers.IO) {
            val prefs = context.dataStore.data.first()
            val json = prefs[USUARIOS_KEY]
            val usuarios = if (json != null) Json.decodeFromString<MutableMap<String, String>>(json) else mutableMapOf()

            if (usuarios.containsKey(usuario)) {
                false
            } else {
                usuarios[usuario] = senha
                context.dataStore.edit { it[USUARIOS_KEY] = Json.encodeToString(usuarios) }
                true
            }
        }
    }

    suspend fun autenticar(usuario: String, senha: String): Boolean {
        return withContext(Dispatchers.IO) {
            val prefs = context.dataStore.data.first()
            val json = prefs[USUARIOS_KEY]
            val usuarios = if (json != null) Json.decodeFromString<Map<String, String>>(json) else emptyMap()
            usuarios[usuario] == senha
        }
    }

    suspend fun salvarLembretes(usuario: String, lista: List<LembreteSerializable>) {
        withContext(Dispatchers.IO) {
            val chave: Preferences.Key<String> = stringPreferencesKey("lembretes_$usuario")
            println("[DEBUG] Salvando lembretes para: $usuario - Total: ${lista.size}")
            context.dataStore.edit { prefs ->
                prefs[chave] = Json.encodeToString(lista)
            }
        }
    }

    suspend fun carregarLembretes(usuario: String): List<LembreteSerializable> {
        return withContext(Dispatchers.IO) {
            val chave: Preferences.Key<String> = stringPreferencesKey("lembretes_$usuario")
            val prefs = context.dataStore.data.first()
            val resultado = prefs[chave]?.let { Json.decodeFromString<List<LembreteSerializable>>(it) } ?: emptyList()
            println("[DEBUG] Carregando lembretes de: $usuario - Total: ${resultado.size}")
            resultado
        }
    }


    suspend fun salvarUsuarioLogado(usuario: String) {
        withContext(Dispatchers.IO) {
            println("[DEBUG] Usuario logado salvo: $usuario")
            context.dataStore.edit {
                it[USUARIO_LOGADO_KEY] = usuario
            }
        }
    }

    suspend fun obterUsuarioLogado(): String? {
        return withContext(Dispatchers.IO) {
            val prefs = context.dataStore.data.first()
            val usuario = prefs[USUARIO_LOGADO_KEY]
            println("[DEBUG] Usuario logado atual: $usuario")
            usuario
        }
    }
}
