package com.example.mylistproject.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.mylistproject.ui.theme.MyColors

@Composable
fun BottomNavBar(navController: NavController) {
    NavigationBar(containerColor = MyColors.Cabecalho) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("bemvindo") },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Início",
                    tint = Color.White
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Em breve: Ajuda */ },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = "Ajuda",
                    tint = Color.White
                )
            }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Em breve: Configurações */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configurações",
                    tint = Color.White
                )
            }
        )
    }
}
