dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        jcenter() // TODO: migrate to mavenCentral
    }
}
rootProject.name = "Pi Hole Connect"
include(":app")
