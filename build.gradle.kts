buildscript {
    extra["composeVersion"] = "1.2.0"
    extra["ktorVersion"] = "2.1.0"
    extra["protoBufJavaLiteVersion"] = "3.21.5"
    extra["hiltVersion"] = "2.43.2"
    extra["lifecycleVersion"] = "2.5.1"
    extra["cameraxVersion"] = "1.2.0-beta01"
    extra["accompanistVersion"] = "0.23.1"
}

plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("com.google.protobuf") version "0.8.19" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.google.dagger.hilt.android") version "2.43.2" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
