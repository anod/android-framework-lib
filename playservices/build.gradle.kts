plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    android {
        namespace = "info.anodsplace.playservices"
        compileSdk = 37
        minSdk = 31
        androidResources {
            enable = true
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.play.services.identity)
                implementation(libs.play.services.auth)

                implementation(project(":lib:context"))
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}