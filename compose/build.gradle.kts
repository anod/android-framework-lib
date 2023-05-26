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
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    namespace = "info.anodsplace.compose"
}

dependencies {
    implementation(project(":lib:applog"))
    implementation(project(":lib:ktx"))
    implementation(project(":lib:permissions"))
    implementation(project(":lib:graphics"))

    implementation("androidx.activity:activity-compose:1.7.2")

    val compose = platform("androidx.compose:compose-bom:2023.05.01")
    implementation(compose)
    androidTestImplementation(compose)

    api("androidx.compose.foundation:foundation:1.5.0-beta01")
    api("androidx.compose.ui:ui")
    api("androidx.compose.material3:material3:1.1.0")
    api("androidx.compose.material3:material3-window-size-class:1.1.0")
    api("androidx.compose.material:material-icons-core")
    api("androidx.compose.material:material-icons-extended:1.4.3")

    api("androidx.compose.ui:ui-tooling-preview")
    debugApi("androidx.compose.ui:ui-tooling")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.21")
}