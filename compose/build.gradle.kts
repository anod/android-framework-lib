plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30
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
        kotlinCompilerVersion = "1.5.31"
        kotlinCompilerExtensionVersion = "1.0.5"
    }
}

dependencies {
    implementation(project(":lib:applog"))

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.activity:activity:1.4.0")
    implementation("androidx.compose.ui:ui:1.0.5")
    implementation("androidx.compose.material:material:1.0.5")
    implementation("androidx.compose.foundation:foundation:1.0.5")
    implementation("androidx.compose.material:material-icons-core:1.0.5")
    implementation("androidx.compose.material:material-icons-extended:1.0.5")

    implementation("androidx.compose.ui:ui-tooling:1.0.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}