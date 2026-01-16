plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.graphics"
        compileSdk = 36
        minSdk = 31
        // PathParser
        withJava()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.palette.ktx)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.annotation)
            }
        }
    }
}