plugins {
  id("org.jetbrains.kotlin.multiplatform").version("1.7.10")
  id("org.jetbrains.kotlin.plugin.serialization").version("1.7.10")
}

repositories {
  mavenCentral()
}

tasks.register("fatJar", Jar::class.java) {
  dependsOn("jvmJar")

  archiveClassifier.set("all")
  manifest.attributes["Main-Class"] = "pga.MainKt"

  with(tasks.named("jvmJar").get() as Jar)
  from(configurations["jvmRuntimeClasspath"].map { zipTree(it) })

  exclude("META-INF/versions/9/module-info.class")
}

kotlin {
  targets {
    listOf(macosArm64(), macosX64()).forEach {
      it.binaries {
        executable {
          entryPoint("pga.main")
        }
      }
    }
    jvm()
  }

  sourceSets {
    val commonMain = getByName("commonMain") {
      dependencies {
        implementation("com.github.ajalt.clikt:clikt:3.5.0")
        implementation("com.squareup.okio:okio:3.2.0")
        implementation("io.ktor:ktor-client-core:2.0.3")
        implementation("io.ktor:ktor-client-cio:2.0.3")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        implementation("com.github.ajalt.mordant:mordant:2.0.0-beta7")
      }
    }
    getByName("commonTest") {
      dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
      }
    }

    val nativeMain = create("nativeMain") {
      dependsOn(commonMain)
    }

    getByName("macosX64Main") {
      dependsOn(nativeMain)
    }
    getByName("macosArm64Main") {
      dependsOn(nativeMain)
    }
  }
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
  binaries.all {
    binaryOptions["memoryModel"] = "experimental"
  }
}

listOf("X64", "Arm64").forEach { arch ->
  tasks.register("${arch.toLowerCase()}zip", Zip::class.java) {
    into("pin-github-actions")
    from(tasks.named("linkReleaseExecutableMacos$arch")) {
      rename {
        println("rename: $it")
        it.replace(".kexe","")
      }
    }
    archiveClassifier.set(arch.toLowerCase())
  }
}
