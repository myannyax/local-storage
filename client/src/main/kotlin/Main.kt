import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
  val start = System.currentTimeMillis()
  val n = 3000000
  val batch = 100
  val count = AtomicInteger()
  val client = HttpClient(Apache) {
    engine {
      followRedirects = true
      socketTimeout = 10_000
      connectTimeout = 10_000
      connectionRequestTimeout = 20_000
      customizeClient {
        setMaxConnTotal(1000)
        setMaxConnPerRoute(100)
      }
    }
  }
  runBlocking {
    val random = Random(42)
    repeat(n / batch) {
      (0 until batch).map {
        val id = random.nextLong()
        count.incrementAndGet()
        async {
          client.request<String>("http://0.0.0.0:8080/") {
            method = HttpMethod.Post
            headers.append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            body = listOf("id" to "$id", "value" to "$id").formUrlEncode()
          }
        }
      }.forEach { it.await() }
    }
  }

  println(count.get())
  runBlocking {
    val random = Random(42)
    repeat(n / batch) {
      (0 until batch).map {
        val id = random.nextLong()
        async {
          client.request<HttpStatement>("http://0.0.0.0:8080/?id=$id") {
            method = HttpMethod.Get
          }.receive<String>()
        }
      }.forEach {
        it.await()
      }
    }
  }
  client.close()

  val end = System.currentTimeMillis()
  println("Total time: ${TimeUnit.MILLISECONDS.toMinutes(end - start)}")
}
