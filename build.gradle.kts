import com.matthewprenger.cursegradle.*
import net.fabricmc.loom.task.RemapJarTask
import java.io.FileNotFoundException

plugins {
  id("fabric-loom") version Versions.LOOM
  id("com.modrinth.minotaur") version Versions.MINOTAUR
  id("com.matthewprenger.cursegradle") version Versions.CURSEGRADLE
  id("io.github.p03w.machete") version "2.0.1"
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

base {
  archivesName.set(ModData.ID)
}

group = ModData.GROUP
version = ModData.VERSION

repositories {
  mavenCentral()
  maven("https://maven.mim1q.dev") // Gimm1q
  maven("https://jitpack.io")
  maven("https://maven.shedaniel.me/")
  maven("https://maven.architectury.dev/")
  maven("https://maven.terraformersmc.com/releases/")
  maven("https://maven.wispforest.io")
  maven("https://maven.blamejared.com") // Patchouli
}

dependencies {
  minecraft("com.mojang:minecraft:${Versions.MINECRAFT}")

  mappings("net.fabricmc:yarn:${Versions.YARN}:v2")

  modImplementation("net.fabricmc:fabric-loader:${Versions.FABRIC_LOADER}")
  modImplementation("net.fabricmc.fabric-api:fabric-api:${Versions.FABRIC_API}")

  modImplementation("com.github.Draylar.omega-config:omega-config-base:${Versions.OMEGA_CONFIG}")
  include("com.github.Draylar.omega-config:omega-config-base:${Versions.OMEGA_CONFIG}")

  modImplementation("io.wispforest:owo-lib:${Versions.OWOLIB}")
  include("io.wispforest:owo-sentinel:${Versions.OWOLIB}")

  modImplementation("vazkii.patchouli:Patchouli:${Versions.PATCHOULI}")

  modCompileOnly("dev.emi:emi-fabric:${Versions.EMI}:api")
  modLocalRuntime("dev.emi:emi-fabric:${Versions.EMI}")

  modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${Versions.REI}")
  modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${Versions.REI}")

  include(modImplementation("dev.mim1q:gimm1q:${Versions.GIMM1Q}")!!)
}

tasks {
  withType<ProcessResources> {
    inputs.property("version", ModData.VERSION)
    filesMatching("fabric.mod.json") {
      expand("version" to ModData.VERSION)
    }
  }
  withType<JavaCompile> {
    configureEach {
      options.release.set(17)
    }
  }
  register("runDatagenScript") {
    group = "fabric"
    dependsOn("datagen:run")
  }
}

// Data generation

sourceSets {
  main {
    resources {
      srcDirs("src/main/generated")
    }
  }
}

// Access widener
loom {
  accessWidenerPath.set(file("src/main/resources/minecells.accesswidener"))
}

// Publishing
val secretsFile = rootProject.file("publishing.properties")
val secrets = Secrets(secretsFile)

val remapJar = tasks.getByName("remapJar") as RemapJarTask
val newVersionName = "${ModData.ID}-${ModData.mcVersions[0]}-${ModData.VERSION}"
val newChangelog = try {
  rootProject.file("changelogs/${ModData.mcVersions[0]}-${ModData.VERSION}.md").readText()
} catch (_: FileNotFoundException) {
  println("No changelog found")
  ""
}

if (secrets.isModrinthReady()) {
  println("Setting up Minotaur")
  modrinth {
    token.set(secrets.modrinthToken)
    projectId.set(secrets.modrinthId)
    uploadFile.set(remapJar)
    versionName.set(newVersionName)
    versionType.set(ModData.VERSION_TYPE)
    changelog.set(newChangelog)
    syncBodyFrom.set(rootProject.file("projectPageAssets/README.md").readText())
    gameVersions.set(ModData.mcVersions)
    loaders.set(listOf("fabric"))
    dependencies {
      ModData.dependencies.forEach(required::project)
    }
  }
}

if (secrets.isCurseforgeReady()) {
  println("Setting up Cursegradle")
  curseforge {
    apiKey = secrets.curseforgeToken
    project(closureOf<CurseProject> {
      id = secrets.curseforgeId
      releaseType = ModData.VERSION_TYPE
      ModData.mcVersions.forEach(::addGameVersion)
      addGameVersion("Fabric")
      changelog = newChangelog
      changelogType = "markdown"
      relations(closureOf<CurseRelation> {
        ModData.dependencies.forEach(::requiredDependency)
      })
      mainArtifact(remapJar, closureOf<CurseArtifact> {
        displayName = newVersionName
      })
    })
    options(closureOf<Options> {
      forgeGradleIntegration = false
    })
  }
  project.afterEvaluate {
    tasks.getByName<CurseUploadTask>("curseforge${secrets.curseforgeId}") {
      dependsOn(remapJar)
    }
    tasks.getByName("modrinth") {
      dependsOn("optimizeOutputsOfRemapJar")
    }
  }
}