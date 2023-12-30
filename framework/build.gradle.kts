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
    namespace = "info.anodsplace.framework"
}

dependencies {
    implementation(project(":lib:applog"))
    implementation(libs.androidx.activity.compose)
    implementation("androidx.window:window:1.2.0")
    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlin.stdlib)
}