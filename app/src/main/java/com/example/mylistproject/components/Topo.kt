@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.mylistproject.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mylistproject.ui.theme.MyColors

@Composable
fun Topo(titulo: String) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = titulo, color = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MyColors.Cabecalho
        )
    )
}
