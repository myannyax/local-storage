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
      val value = hashTable.get(id)
      call.respondText(value ?: "not found")
    }
    put("/") {
      val request = call.receive<PutRequest>()
      hashTable.put(request.id, request.value)
    }
  }
}
