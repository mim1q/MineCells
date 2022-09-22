import java.io.File
import java.util.*

class Secrets(file: File) {
  companion object {
    private const val MODRINTH_TOKEN_KEY = "MODRINTH_TOKEN"
    private const val MODRINTH_ID_KEY = "MODRINTH_ID"
    private const val CURSEFORGE_TOKEN_KEY = "CURSEFORGE_TOKEN"
    private const val CURSEFORGE_ID_KEY = "CURSEFORGE_ID"
  }

  val modrinthToken: String?
  val modrinthId: String?
  val curseforgeToken: String?
  val curseforgeId: String?

  init {
    val properties = getProperties(file)
    this.modrinthToken = properties.getProperty(MODRINTH_TOKEN_KEY)
    this.modrinthId = properties.getProperty(MODRINTH_ID_KEY)
    this.curseforgeToken = properties.getProperty(CURSEFORGE_TOKEN_KEY)
    this.curseforgeId = properties.getProperty(CURSEFORGE_ID_KEY)
  }

  private fun getProperties(file: File): Properties {
    if (!file.exists()) {
      println("$file not found.\n" +
        "The file structure should look as follows:\n" +
        "$MODRINTH_TOKEN_KEY=<your modrinth token>\n" +
        "$MODRINTH_ID_KEY=<your modrinth id>\n" +
        "$CURSEFORGE_TOKEN_KEY=<your curseforge token>\n" +
        "$CURSEFORGE_ID_KEY=<your curseforge id>")
      return Properties()
    }
    val properties = Properties()
    properties.load(file.inputStream())
    return properties
  }

  fun isModrinthReady(): Boolean {
    return modrinthToken != null && modrinthId != null
  }

  fun isCurseforgeReady(): Boolean {
    return curseforgeToken != null && curseforgeId != null
  }
}