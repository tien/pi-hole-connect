buildscript {
    val kotlinVersion: String by project
    extra["composeVersion"] = "1.0.0-beta08"
    extra["ktorVersion"] = "1.6.0"
    extra["protoBufJavaLiteVersion"] = "3.17.3"
    val hiltVersion by extra("2.36")
    extra["lifecycleVersion"] = "2.3.1"
    extra["cameraxVersion"] = "1.0.0"
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-beta03")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}