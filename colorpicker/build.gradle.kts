
plugins {
    id("com.android.library")
}

repositories {
    google()
    jcenter()
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.0-beta01")
}
