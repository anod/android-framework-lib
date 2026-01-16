plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.notification"
        compileSdk = 36
        minSdk = 31
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":lib:context"))
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}