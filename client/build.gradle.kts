import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  application
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.ktor:ktor-client-core:1.6.6")
  implementation("io.ktor:ktor-client-cio:1.6.6")
  implementation("io.ktor:ktor-client-logging:1.6.6")
  implementation("io.ktor:ktor-client-apache:1.6.6")


  testImplementation(kotlin("test"))
}

tasks.test {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "10"
}

application {
  mainClass.set("MainKt")
}