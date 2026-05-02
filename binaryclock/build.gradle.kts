plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
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
            implementation("androidx.glance:glance-appwidget:1.1.1")
        }
    }
}
