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
    namespace = "info.anodsplace.framework"
}

dependencies {
    implementation(project(":lib:applog"))
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.window:window:1.2.0-rc01")
    implementation("androidx.core:core-ktx:1.12.0")

    implementation("com.google.android.gms:play-services-identity:18.0.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation(libs.kotlin.stdlib)
}