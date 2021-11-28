package com.example

import io.ktor.application.*

interface HashTable {
  suspend fun get(id: Long, call: ApplicationCall)

  suspend fun put(id: Long, value: String, call: ApplicationCall)

  fun restore()

  fun start()
}