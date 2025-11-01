import com.google.protobuf.gradle.id
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val openApiOutput = file("${layout.buildDirectory.asFile.get().path}/generated/source/open-api")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.protobuf)
    idea
}

android {
    namespace = "com.tien.piholeconnect"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.tien.piholeconnect"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "SNAPSHOT"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    androidResources { localeFilters += listOf("en", "de", "pl", "ro") }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk { debugSymbolLevel = "FULL" }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs +=
            listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlin.time.ExperimentalTime",
                "-opt-in=io.ktor.utils.io.InternalAPI",
            )
    }

    buildFeatures { compose = true }

    packaging { resources { excludes.add("/META-INF/{AL2.0,LGPL2.1}") } }

    sourceSets.getByName("main") { kotlin { srcDir(File(openApiOutput, "debug/kotlin")) } }

    tasks { preBuild { dependsOn(openApiGenerate) } }
}

// Temporary workaround
// https://github.com/google/ksp/issues/1590
androidComponents {
    onVariants(selector().all()) { variant ->
        afterEvaluate {
            val capName = variant.name.capitalized()
            tasks.getByName<KotlinCompile>("ksp${capName}Kotlin") {
                setSource(tasks.getByName("generate${capName}Proto").outputs)
            }
        }
    }
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:${libs.versions.protoBufJavaLite.get()}" }

    generateProtoTasks { all().forEach { task -> task.builtins { id("java") { option("lite") } } } }
}

openApiGenerate {
    generatorName = "kotlin"
    remoteInputSpec =
        "https://raw.githubusercontent.com/tien/FTL/refs/heads/fix/batch-delete-request-body/src/api/docs/content/specs/main.yaml"
    outputDir = openApiOutput.absolutePath
    ignoreFileOverride = "${projectDir.path}/openapi-generator-ignore"
    library = "multiplatform"
    packageName = "${android.namespace}.repository"
    additionalProperties =
        mapOf(
            "sourceFolder" to "debug/kotlin",
            "enumPropertyNaming" to "UPPERCASE",
            "dateLibrary" to "kotlinx-datetime",
            "useSettingsGradle" to true,
        )
    typeMappings =
        mapOf(
            "AddressMaybeArrayAddress" to "List<String>",
            "DomainMaybeArrayDomain" to "List<String>",
        )
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.android.billingclient.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.vico)
    implementation(libs.google.android.material)
    implementation(libs.google.dagger.hilt)
    implementation(libs.google.protobuf.javalite)

    ksp(libs.google.dagger.hilt.compiler)

    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.bundles.androidTest)
}
