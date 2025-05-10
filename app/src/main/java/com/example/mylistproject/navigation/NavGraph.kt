package com.example.mylistproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mylistproject.view.BemVindo
import com.example.mylistproject.view.Calendario
import com.example.mylistproject.view.Lembretes
import com.example.mylistproject.view.Login
import com.example.mylistproject.view.Registro

object Rotas {
    const val Login = "Login"
    const val Registro = "Registro"
    const val BemVindo = "Bemvindo"
    const val Lembretes = "Lembretes"
    const val Calendario = "Calendario"
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Rotas.Login) {
        composable(Rotas.Login) { Login(navController) }
        composable(Rotas.Registro) { Registro(navController) }
        composable(Rotas.BemVindo) { BemVindo(navController) }
        composable(Rotas.Lembretes) { Lembretes(navController) }
        composable(Rotas.Calendario) { Calendario(navController) }
    }
}
