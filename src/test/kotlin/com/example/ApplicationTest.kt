package com.example

import com.example.internal.PartitionedHashTable
import com.example.internal.PersistentHashTable
import com.example.model.PutRequest
import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Random

class ApplicationTest {
  @Test
  fun testRoot() {
    withTestApplication({ configureRouting(PartitionedHashTable("test_table")) }) {
      var random = Random(42)
      val n = 1000000
      for (i in 0..n) {
        val id = random.nextLong()
        handleRequest(HttpMethod.Post, "/") {
          addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
          setBody(listOf("id" to "$id", "value" to "$id").formUrlEncode())
        }
      }
      random = Random(42)
      for (i in 0..n) {
        val id = random.nextLong()
        handleRequest(HttpMethod.Get, "/?id=$id").apply {
          assertEquals(HttpStatusCode.OK, response.status())
          assertEquals(id.toString(), response.content)
        }
      }
    }
  }
}