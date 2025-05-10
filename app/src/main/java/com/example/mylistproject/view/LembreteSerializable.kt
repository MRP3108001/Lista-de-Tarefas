package com.example.mylistproject.view

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LembreteSerializable(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val descricao: String,
    val dataHora: String,
    val concluido: Boolean = false,
    val dataConclusao: String? = null
)
