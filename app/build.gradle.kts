plugins {
    id("com.android.application")
}

android {
    namespace = "cl.nuky.didioma"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.nuky.didioma"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
