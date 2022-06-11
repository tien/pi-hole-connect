buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.1.0"
    extra["ktorVersion"] = "2.0.2"
    extra["protoBufJavaLiteVersion"] = "3.21.1"
    val hiltVersion by extra("2.40.5")
    extra["lifecycleVersion"] = "2.4.1"
    extra["cameraxVersion"] = "1.1.0-beta01"
    extra["accompanistVersion"] = "0.23.1"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
