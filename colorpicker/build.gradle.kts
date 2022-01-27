
plugins {
    id("com.android.library")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.fragment:fragment:1.4.1")
}
