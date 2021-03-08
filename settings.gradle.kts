dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // TODO: migrate to mavenCentral
    }
}
rootProject.name = "Pi Hole Connect"
include(":app")
