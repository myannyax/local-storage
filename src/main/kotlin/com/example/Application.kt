package com.example

import com.example.internal.PartitionedHashTable
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*

fun main(args: Array<String>) {
  embeddedServer(Netty, port = args[0].toIntOrNull() ?: 8080, host = "0.0.0.0") {
    val hashTable = PartitionedHashTable("main_table")
    configureRouting(hashTable)
  }.start(wait = true)
}
