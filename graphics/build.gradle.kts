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
    namespace = "info.anodsplace.graphics"
}

dependencies {
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.annotation:annotation:1.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
}