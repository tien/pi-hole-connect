buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.1.0-beta02"
    extra["ktorVersion"] = "1.6.7"
    extra["protoBufJavaLiteVersion"] = "3.19.1"
    val hiltVersion by extra("2.40.1")
    extra["lifecycleVersion"] = "2.4.0"
    extra["cameraxVersion"] = "1.0.2"
    extra["accompanistVersion"] = "0.20.2"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
