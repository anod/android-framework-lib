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
    namespace = "info.anodsplace.permissions"
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
}