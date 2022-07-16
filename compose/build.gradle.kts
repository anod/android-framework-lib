plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 27
        targetSdk = 32
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
        kotlinCompilerExtensionVersion = "1.2.0"
    }
    namespace = "info.anodsplace.compose"
}

dependencies {
    implementation(project(":lib:applog"))
    implementation(project(":lib:ktx"))
    implementation(project(":lib:permissions"))

    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.24.9-beta")
    api("androidx.compose.ui:ui:1.3.0-alpha01")
    api("androidx.compose.material3:material3:1.0.0-alpha14")
    api("androidx.compose.foundation:foundation:1.3.0-alpha01")
    api("androidx.compose.material:material-icons-core:1.3.0-alpha01")
    api("androidx.compose.material:material-icons-extended:1.3.0-alpha01")

    debugApi("androidx.compose.ui:ui-tooling:1.1.1")
    debugApi("androidx.customview:customview:1.2.0-alpha01")
    debugApi("androidx.customview:customview-poolingcontainer:1.0.0-rc01")
    api("androidx.compose.ui:ui-tooling-preview:1.1.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
}