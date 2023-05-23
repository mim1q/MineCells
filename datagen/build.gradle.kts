import java.nio.file.Files
import java.nio.file.Path

plugins {
  kotlin("jvm") version "1.8.21"
  id("application")
}

group = "com.github.mim1q.derelict"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
  implementation(files("libs/tada.jar"))
  implementation("com.google.code.gson:gson:2.10.1")
}

kotlin {
  jvmToolchain(11)
}

application {
  mainClass.set("datagen.MainKt")
}

fun deleteDir(directory: Path) {
  Files.walk(directory)
    .sorted(Comparator.reverseOrder())
    .map { it.toFile() }
    .forEach { it.delete() }
}

val generatedDir = projectDir.resolve("../src/main/generated")
tasks {
  withType<JavaExec> {
    doFirst {
      try {
        deleteDir(generatedDir.toPath())
      } catch (_: Exception) {
        println("Datagen output directory hasn't been generated yet")
      }
    }
    args = listOf(generatedDir.path)
  }
}