plugins {
    id("com.android.library")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 27
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "info.anodsplace.colorpicker"
}

dependencies {
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.fragment:fragment:1.5.4")
}