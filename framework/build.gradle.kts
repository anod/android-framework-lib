plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.framework"
        compileSdk = 36
        minSdk = 31
        androidResources {
            enable = true
        }
        withJava()
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":lib:applog"))
                implementation(libs.androidx.activity)
                implementation(libs.androidx.window)
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}