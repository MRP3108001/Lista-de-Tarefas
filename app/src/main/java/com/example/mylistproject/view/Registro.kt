package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylistproject.navigation.Rotas
import com.example.mylistproject.storage.DataStoreManager
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
fun Registro(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { DataStoreManager(context) }

    var novoUsuario by remember { mutableStateOf("") }
    var novaSenha by remember { mutableStateOf("") }
    var confirmacao by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }
    var sucesso by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar nova conta", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = novoUsuario,
            onValueChange = { novoUsuario = it },
            label = { Text("Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = novaSenha,
            onValueChange = { novaSenha = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmacao,
            onValueChange = { confirmacao = it },
            label = { Text("Confirmar senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    if (novoUsuario.isBlank() || novaSenha.isBlank()) {
                        erro = "Usuário e senha não podem estar vazios."
                        sucesso = false
                        return@launch
                    }

                    if (novaSenha != confirmacao) {
                        erro = "As senhas não coincidem."
                        sucesso = false
                        return@launch
                    }

                    val registrado = dataStore.registrarUsuario(novoUsuario, novaSenha)

                    if (registrado) {
                        erro = null
                        sucesso = true
                        navController.navigate(Rotas.Login) {
                            popUpTo(Rotas.Registro) { inclusive = true }
                        }
                    } else {
                        erro = "Usuário já existe."
                        sucesso = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        erro?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (sucesso) {
            Text("Usuário registrado com sucesso!", color = MaterialTheme.colorScheme.primary)
        }
    }
}
