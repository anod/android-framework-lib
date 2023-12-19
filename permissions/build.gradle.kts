plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 34

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
    namespace = "info.anodsplace.permissions"
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation(libs.kotlin.stdlib)
}