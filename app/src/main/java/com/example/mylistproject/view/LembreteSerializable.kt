package com.example.mylistproject.view

import kotlinx.serialization.Serializable

@Serializable
data class LembreteSerializable(
    val titulo: String,
    val descricao: String,
    val dataHora: String,
    val concluido: Boolean = false,
    val dataConclusao: String? = null
)
