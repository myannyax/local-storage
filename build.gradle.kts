val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
  application
  kotlin("jvm") version "1.6.0"
  id("org.jetbrains.kotlin.plugin.serialization") version "1.5.30"
}

group = "com.example"
version = "0.0.1"
application {
  mainClass.set("com.example.ApplicationKt")
  applicationDefaultJvmArgs = listOf("-Xms1024m", "-Xmx2048m")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.ktor:ktor-server-core:$ktor_version")
  implementation("io.ktor:ktor-server-netty:$ktor_version")
  implementation("ch.qos.logback:logback-classic:$logback_version")

  implementation("io.ktor:ktor-serialization:$ktor_version")

  testImplementation("io.ktor:ktor-server-tests:$ktor_version")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.withType<Test> {
  jvmArgs("-Xms1024m")
  jvmArgs("-Xmx2048m")
}