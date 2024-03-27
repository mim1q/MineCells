package datagen

import java.io.File

fun getImageFile(name: String): File =
  object {}.javaClass.getResource("/images/$name")?.file?.let { File(it) }
    ?: throw IllegalArgumentException("Image not found: $name")
