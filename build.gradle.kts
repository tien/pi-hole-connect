plugins { alias(libs.plugins.spotless) }

spotless {
    kotlin {
        target("**/*.kt")
        ktfmt("0.62").kotlinlangStyle()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktfmt("0.62").kotlinlangStyle()
    }
}
