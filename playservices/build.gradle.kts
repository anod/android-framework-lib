plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.playservices"
        compileSdk = 36
        minSdk = 31
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