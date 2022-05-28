
plugins {
    id("com.android.library")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "info.anodsplace.colorpicker"
}

dependencies {
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.fragment:fragment:1.4.1")
}