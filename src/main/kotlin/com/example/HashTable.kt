package com.example

interface HashTable {
    suspend fun get(id: Long): String?

    suspend fun put(id: Long, value: String)

    fun restore()
}