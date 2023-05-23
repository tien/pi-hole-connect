buildscript {
    extra["composeVersion"] = "1.2.0"
    extra["ktorVersion"] = "2.3.0"
    extra["protoBufJavaLiteVersion"] = "3.23.1"
    extra["hiltVersion"] = "2.46.1"
    extra["lifecycleVersion"] = "2.6.1"
    extra["cameraxVersion"] = "1.2.2"
    extra["accompanistVersion"] = "0.27.0"
}

plugins {
    id("com.android.application") version "8.0.1" apply false
    id("com.android.library") version "8.0.1" apply false
    id("com.google.protobuf") version "0.9.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
