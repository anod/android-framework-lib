
plugins {
    id("com.android.library")
}

repositories {
    jcenter()
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
    implementation("com.google.android.material:material:1.4.0-rc01")
    implementation("androidx.fragment:fragment:1.3.5")
}
