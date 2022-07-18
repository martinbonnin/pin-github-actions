plugins {
  id("org.jetbrains.kotlin.multiplatform").version("1.7.10")
  id("org.jetbrains.kotlin.plugin.serialization").version("1.7.10")
  id("distribution")
}

repositories {
  mavenCentral()
}

tasks.register("allJar", Jar::class.java) {
  dependsOn("jvmJar")

  archiveClassifier.set("all")
  manifest.attributes["Main-Class"] = "pga.MainKt"

  with(tasks.named("jvmJar").get() as Jar)
  from(configurations["jvmRuntimeClasspath"].map { zipTree(it) })

  exclude("META-INF/versions/9/module-info.class")
}

val startScriptTaskProvider =
  tasks.register("createStartScript", org.gradle.jvm.application.tasks.CreateStartScripts::class.java) {
    outputDir = file("build/start_scripts/")
    mainClass.set("pga.MainKt")
    applicationName = "pin-github-actions"
    classpath = files(configurations["jvmRuntimeClasspath"], tasks.named("jvmJar").map { it.outputs.files.files })
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
    val ktorVersion = "2.0.3"
    val commonMain = getByName("commonMain") {
      dependencies {
        implementation("com.github.ajalt.clikt:clikt:3.5.0")
        implementation("com.squareup.okio:okio:3.2.0")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
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

    val darwinMain = create("darwinMain") {
      dependsOn(commonMain)
      dependencies {
        implementation("io.ktor:ktor-client-darwin:$ktorVersion")
      }
    }

    getByName("macosX64Main") {
      dependsOn(darwinMain)
    }
    getByName("macosArm64Main") {
      dependsOn(darwinMain)
    }

    getByName("jvmMain") {
      dependencies {
        implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
      }
    }
  }
}

distributions.named("main").configure {
  contents {
    from(configurations["jvmRuntimeClasspath"]) {
      into("lib")
    }
    from(tasks.named("jvmJar")) {
      into("lib")
    }
    from(startScriptTaskProvider) {
      into("bin")
    }
  }
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
  binaries.all {
    binaryOptions["memoryModel"] = "experimental"
  }
}
