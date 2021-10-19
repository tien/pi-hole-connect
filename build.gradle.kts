buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.0.0-rc02"
    extra["ktorVersion"] = "1.6.1"
    extra["protoBufJavaLiteVersion"] = "3.17.3"
    val hiltVersion by extra("2.37")
    extra["lifecycleVersion"] = "2.3.1"
    extra["cameraxVersion"] = "1.0.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.2.0-alpha02")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
