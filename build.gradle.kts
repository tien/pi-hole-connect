buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.0.0-beta06"
    extra["ktorVersion"] = "1.5.4"
    extra["protoBufJavaLiteVersion"] = "3.15.8"
    val hiltVersion by extra("2.35.1")
    extra["lifecycleVersion"] = "2.3.1"
    extra["cameraxVersion"] = "1.0.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha15")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}