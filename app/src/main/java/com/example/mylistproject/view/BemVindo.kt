package com.example.mylistproject.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylistproject.ui.theme.MyColors
import com.example.mylistproject.components.Topo
import com.example.mylistproject.components.BottomNavBar

@Composable
fun BemVindo(navController: NavController) {
    Scaffold(
        topBar = { Topo("Bem vindo(a)!") },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tela Bem-vindo - Iteração 2",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                BotaoMenu(
                    texto = "Lembretes",
                    icone = Icons.Default.Warning
                ) {
                    println("Navegando para Lembretes - Iteração 2")
                    navController.navigate("lembretes")
                }

                BotaoMenu(
                    texto = "Calendário",
                    icone = Icons.Default.DateRange
                ) { navController.navigate("calendario") }
            }
        }
    }
}

@Composable
fun BotaoMenu(texto: String, icone: ImageVector, aoClicar: () -> Unit) {
    Button(
        onClick = aoClicar,
        modifier = Modifier
            .width(140.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MyColors.BotaoPrincipal)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icone,
                contentDescription = texto,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
