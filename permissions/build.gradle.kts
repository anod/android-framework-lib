plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.permissions"
        compileSdk = 36
        minSdk = 31
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "permissions"
            isStatic = true
        }
    }
    jvm()
    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.activity)
        }
    }
}