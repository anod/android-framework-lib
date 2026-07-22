plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    android {
        namespace = "info.anodsplace.ktx"
        compileSdk = 37
        minSdk = 31
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "ktx"
            isStatic = true
        }
    }
    sourceSets.all {
        languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
//        commonTest.dependencies {
//            implementation(libs.kotlin.test)
//        }
    }
}