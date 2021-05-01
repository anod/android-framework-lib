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
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("androidx.activity:activity-ktx:1.2.2")
    implementation("androidx.fragment:fragment-ktx:1.3.3")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.window:window:1.0.0-alpha05")
    implementation("androidx.core:core-ktx:1.3.2")

    implementation("com.google.android.gms:play-services-identity:17.0.0")
    implementation("com.google.android.gms:play-services-auth:19.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.32")
}