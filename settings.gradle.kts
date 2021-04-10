dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://kotlin.bintray.com/kotlinx")
    }
}
pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        id("com.google.protobuf") version "0.8.15"
        kotlin("plugin.serialization") version kotlinVersion
    }
}
rootProject.name = "Pi-hole Connect"
include(":app")
