val composeVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra
val protoBufVersion: String by rootProject.extra
val protoBufJavaLiteVersion: String by rootProject.extra
val hiltVersion: String by rootProject.extra
val lifecycleVersion: String by rootProject.extra

plugins {
    id("com.android.application")
    id("com.google.protobuf") version "0.8.15"
    id("dagger.hilt.android.plugin")
    kotlin("android")
    kotlin("plugin.serialization") version "1.4.30"
    kotlin("kapt")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.tien.piholeconnect"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled(false)
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

apply(from = "protobuf.gradle")

dependencies {
    implementation(kotlin("reflect"))
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.activity:activity-compose:1.3.0-alpha03")
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha08")
    implementation("androidx.datastore:datastore:1.0.0-alpha08")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")
    implementation("com.google.protobuf:protobuf-javalite:$protoBufJavaLiteVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}