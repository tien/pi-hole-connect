import com.google.protobuf.gradle.id
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

abstract class OpenApiKotlinSources : DefaultTask() {
    @get:OutputDirectory abstract val sourceDir: DirectoryProperty
}

val openApiOutput = layout.buildDirectory.dir("generated/source/open-api")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
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
        minSdk = 23
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

    buildFeatures { compose = true }

    packaging { resources { excludes.add("/META-INF/{AL2.0,LGPL2.1}") } }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

val openApiKotlinSources =
    tasks.register<OpenApiKotlinSources>("openApiKotlinSources") {
        dependsOn("openApiGenerate")
        sourceDir = openApiOutput.map { it.dir("debug/kotlin") }
    }

androidComponents {
    onVariants { variant ->
        variant.sources.kotlin?.addGeneratedSourceDirectory(
            openApiKotlinSources,
            OpenApiKotlinSources::sourceDir,
        )
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
    outputDir = openApiOutput
    ignoreFileOverride = layout.projectDirectory.file("openapi-generator-ignore")
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
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
