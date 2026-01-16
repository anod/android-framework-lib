plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    androidLibrary {
        namespace = "info.anodsplace.compose"
        compileSdk = 36
        minSdk = 31
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(project(":lib:applog"))
                implementation(project(":lib:ktx"))
                implementation(project(":lib:permissions"))
                implementation(project(":lib:graphics"))

                implementation(libs.androidx.activity)

                implementation(project.dependencies.platform(libs.androidx.compose.bom))

                api(libs.androidx.compose.foundation)
                api(libs.androidx.compose.ui)
                api(libs.androidx.compose.material3)
                api(libs.androidx.compose.material3.window.size)
                api(libs.androidx.compose.material.icons.core)
                api(libs.androidx.compose.material.icons.extended)

                api(libs.androidx.compose.ui.tooling.preview)
            }
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(libs.androidx.compose.ui.tooling)
}