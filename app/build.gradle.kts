val composeVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra
val protoBufVersion: String by rootProject.extra
val protoBufJavaLiteVersion: String by rootProject.extra
val hiltVersion: String by rootProject.extra
val lifecycleVersion: String by rootProject.extra
val cameraxVersion: String by rootProject.extra

plugins {
    id("com.android.application")
    id("com.google.protobuf")
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("kapt")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.tien.piholeconnect"
        minSdk = 21
        targetSdk = 30
        versionCode = 16
        versionName = "6.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            debugSymbolLevel = "FULL"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.time.ExperimentalTime"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

apply(from = "protobuf.gradle")

// FIXME Temporary fix for https://issuetracker.google.com/issues/187101535
configurations.all {
    exclude("androidx.compose.ui", "ui")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
//    implementation("androidx.compose.ui:ui:$composeVersion")
    // FIXME Temporary fix for https://issuetracker.google.com/issues/187101535
    implementation(files("./libs/androidx.compose.ui-ui.aar"))
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")
    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha10")
    implementation("androidx.datastore:datastore:1.0.0-beta01")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha01")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.0.0-alpha24")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")
    implementation("com.google.protobuf:protobuf-javalite:$protoBufJavaLiteVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("com.google.mlkit:barcode-scanning:16.1.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.android.billingclient:billing-ktx:3.0.3")

    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}