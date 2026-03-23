plugins { alias(libs.plugins.spotless) }

spotless {
    kotlin {
        target("**/*.kt")
        ktfmt().kotlinlangStyle()
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktfmt().kotlinlangStyle()
    }
}
