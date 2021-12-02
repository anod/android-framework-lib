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
}

dependencies {
    implementation(project(":lib:applog"))
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.fragment:fragment-ktx:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.window:window:1.0.0-beta04")
    implementation("androidx.core:core-ktx:1.7.0")

    implementation("com.google.android.gms:play-services-identity:17.0.1")
    implementation("com.google.android.gms:play-services-auth:19.2.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
}