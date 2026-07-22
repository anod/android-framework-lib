plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    android {
        namespace = "info.anodsplace.context"
        compileSdk = 37
        minSdk = 31
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}