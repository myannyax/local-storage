package com.example.plugins

import com.example.HashTable
import com.example.model.PutRequest
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting(hashTable: HashTable) {
  hashTable.restore()
  hashTable.start()
  routing {
    get("/") {
      val id = call.request.queryParameters["id"]?.toLong() ?: error("kek")
      hashTable.get(id, call)
      call.respondBytes {  }
    }
    put("/") {
      val request = call.receive<PutRequest>()
      hashTable.put(request.id, request.value, call)
    }
  }
}
