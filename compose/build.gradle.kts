plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 27
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    namespace = "info.anodsplace.compose"
}

dependencies {
    implementation(project(":lib:applog"))
    implementation(project(":lib:ktx"))
    implementation(project(":lib:permissions"))
    implementation(project(":lib:graphics"))

    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.27.1")
    api("androidx.compose.ui:ui:1.3.1")
    api("androidx.compose.material3:material3:1.0.1")
    api("androidx.compose.material3:material3-window-size-class:1.0.1")
    api("androidx.compose.foundation:foundation:1.3.1")
    api("androidx.compose.material:material-icons-core:1.3.1")
    api("androidx.compose.material:material-icons-extended:1.3.1")

    debugApi("androidx.compose.ui:ui-tooling:1.3.1")
    debugApi("androidx.customview:customview:1.2.0-alpha02")
    debugApi("androidx.customview:customview-poolingcontainer:1.0.0")
    api("androidx.compose.ui:ui-tooling-preview:1.3.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
}