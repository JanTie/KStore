plugins {
  kotlin("multiplatform")
  id("com.android.library")
  id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.0"
}

android {
  compileSdk = 33
  defaultConfig {
    minSdk = 21
    targetSdk = 33
  }

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  lint {
    // TODO: Figure out why the linter is failing on CI
    abortOnError = false
  }

  namespace = "io.github.xxfast.kstore.file"
}

kotlin {
  explicitApi()

  android {
    compilations.all {
      kotlinOptions {
        jvmTarget = "1.8"
      }
    }
  }

  jvm("desktop") {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }

  js(IR) {
    nodejs()
  }

  val macosX64 = macosX64()
  val macosArm64 = macosArm64()
  val iosArm64 = iosArm64()
  val iosX64 = iosX64()
  val iosSimulatorArm64 = iosSimulatorArm64()
  val watchosArm32 = watchosArm32()
  val watchosArm64 = watchosArm64()
  val watchosX64 = watchosX64()
  val watchosSimulatorArm64 = watchosSimulatorArm64()
  val tvosArm64 = tvosArm64()
  val tvosX64 = tvosX64()
  val tvosSimulatorArm64 = tvosSimulatorArm64()
  val appleTargets = listOf(
    macosX64, macosArm64,
    iosArm64, iosX64, iosSimulatorArm64,
    watchosArm32, watchosArm64, watchosX64,
    watchosSimulatorArm64,
    tvosArm64, tvosX64, tvosSimulatorArm64,
  )

  appleTargets.forEach { target ->
    with(target) {
      binaries {
        framework {
          baseName = "KStore"
        }
      }
    }
  }

  linuxX64("linux")
  mingwX64("windows")

  sourceSets {
    sourceSets {
      val commonMain by getting {
        dependencies {
          implementation(project(":kstore"))
          implementation("com.squareup.okio:okio:3.3.0")
          implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
          implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
          implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-okio:1.5.0")
        }
      }

      val commonTest by getting {
        dependencies {
          implementation(kotlin("test"))
          implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
          implementation("app.cash.turbine:turbine:0.12.1")
        }
      }

      val androidMain by getting
      val androidTest by getting

      val desktopMain by getting {
        dependencies {
          implementation("com.squareup.okio:okio:3.3.0")
        }
      }
      val desktopTest by getting

      val jsMain by getting {
        dependencies {
          implementation("com.squareup.okio:okio-nodefilesystem:3.3.0")
        }
      }
      val jsTest by getting

      val appleMain by creating {
        dependsOn(commonMain)
      }
      val appleTest by creating

      appleTargets.forEach { target ->
        getByName("${target.targetName}Main") { dependsOn(appleMain) }
        getByName("${target.targetName}Test") { dependsOn(appleTest) }
      }

      val linuxMain by getting {
        dependencies {
          implementation("com.squareup.okio:okio:3.3.0")
        }
      }

      val linuxTest by getting

      val windowsMain by getting {
        dependencies {
          implementation("com.squareup.okio:okio:3.3.0")
        }
      }

      val windowsTest by getting
    }
  }
}