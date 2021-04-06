buildscript {
    allprojects {
        extra["composeVersion"] = "1.0.0-beta03"
        extra["ktorVersion"] = "1.5.3"
        extra["protoBufJavaLiteVersion"] = "3.15.6"
        extra["hiltVersion"] = "2.33-beta"
        extra["lifecycleVersion"] = "2.3.1"
        extra["cameraxVersion"] = "1.0.0-rc03"
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha13")
        classpath(kotlin("gradle-plugin", version = "1.4.31"))
        classpath("com.google.dagger:hilt-android-gradle-plugin:${project.extra["hiltVersion"]}")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}