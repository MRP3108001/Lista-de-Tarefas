package com.example.mylistproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mylistproject.view.BemVindo
import com.example.mylistproject.view.Lembretes
import com.example.mylistproject.view.Calendario

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Bemvindo") {
        composable("BemVindo") { BemVindo(navController) }
        composable("Lembretes") { Lembretes(navController) }
        composable("Calendario") { Calendario(navController) }
    }
}
