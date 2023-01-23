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
    implementation("com.google.accompanist:accompanist-flowlayout:0.28.0")

    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    api("androidx.compose.foundation:foundation")
    api("androidx.compose.ui:ui")
    api("androidx.compose.material3:material3")
    api("androidx.compose.material3:material3-window-size-class")
    api("androidx.compose.material:material-icons-core")
    api("androidx.compose.material:material-icons-extended")

    api("androidx.compose.ui:ui-tooling-preview")
    debugApi("androidx.compose.ui:ui-tooling")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
}