buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha08")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
    }
}

allprojects {
    extra["composeVersion"] = "1.0.0-beta01"
    extra["ktorVersion"] =  "1.5.2"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}