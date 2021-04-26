buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.0.0-beta05"
    extra["ktorVersion"] = "1.5.3"
    extra["protoBufJavaLiteVersion"] = "3.15.8"
    val hiltVersion by extra("2.33-beta")
    extra["lifecycleVersion"] = "2.3.1"
    extra["cameraxVersion"] = "1.0.0-rc05"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}