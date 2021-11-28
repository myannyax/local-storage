package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PutRequest(val id: Long, val value: String)