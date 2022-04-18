plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 27
        targetSdk = 31
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
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.window:window:1.0.0")
    implementation("androidx.core:core-ktx:1.7.0")

    implementation("com.google.android.gms:play-services-identity:18.0.1")
    implementation("com.google.android.gms:play-services-auth:20.1.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
}