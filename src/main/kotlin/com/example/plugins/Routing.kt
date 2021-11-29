package com.example.plugins

import com.example.HashTable
import com.example.model.PutRequest
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting(hashTable: HashTable) {
  hashTable.restore()
  routing {
    get("/") {
      val id = call.request.queryParameters["id"]?.toLong() ?: error("kek")
      hashTable.get(id, call)
    }
    post("/") {
      val request = call.receiveParameters()
      hashTable.put(request["id"]?.toLong() ?: error("kek"), request["value"] ?: error("kek"), call)
    }
  }
}
