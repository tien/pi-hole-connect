buildscript {
    allprojects {
        extra["composeVersion"] = "1.0.0-beta01"
        extra["ktorVersion"] = "1.5.2"
        extra["protoBufJavaLiteVersion"] = "3.15.5"
        extra["hiltVersion"] = "2.33-beta"
        extra["lifecycleVersion"] = "2.3.0"
        extra["cameraxVersion"] = "1.0.0-rc03"
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha11")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${project.extra["hiltVersion"]}")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}