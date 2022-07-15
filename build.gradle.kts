plugins {
  id("org.jetbrains.kotlin.multiplatform").version("1.7.10")
}

repositories {
  mavenCentral()
}

kotlin {
  targets {
    listOf(macosArm64(), macosX64()).forEach {
      it.binaries {
        executable {
          entryPoint("net.mbonnin.pga.main")
        }
      }
    }
  }

  sourceSets {
    val nativeMain = create("nativeMain") {
      dependencies {
        implementation("com.github.ajalt.clikt:clikt:3.5.0")
        implementation("com.squareup.okio:okio:3.2.0")
      }
    }
    getByName("macosArm64Main") {
      dependsOn(nativeMain)
    }
    getByName("macosX64Main") {
      dependsOn(nativeMain)
    }
  }
}