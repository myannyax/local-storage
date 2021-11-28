package com.example

import com.example.internal.PersistentHashTable
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
    val hashTable = PersistentHashTable("main_table")
    configureMonitoring()
    configureRouting(hashTable)
  }.start(wait = true)
}
