package com.example.mylistproject.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.mylistproject.ui.theme.MyColors

@Composable
fun CaixaSelecaoRedonda(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val corDeFundo = if (checked) MyColors.BotaoPrincipal else Color.LightGray
    val corDoIcone = if (checked) Color.White else Color.Transparent

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(corDeFundo)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = if (checked) "Marcado" else "Desmarcado",
            tint = corDoIcone,
            modifier = Modifier.size(16.dp)
        )
    }
}
