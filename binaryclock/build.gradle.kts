plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.binaryclock"
        compileSdk = 36
        minSdk = 31
        androidResources {
            enable = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        androidMain.dependencies {
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.coroutines.core)
        }
    }
}
