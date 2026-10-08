plugins {
    id("com.android.application")
}

android {
    namespace = "cl.nuky.didioma"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.nuky.didioma.keyboard"
        minSdk = 24
        targetSdk = 35
        versionCode = 5
        versionName = "1.5"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
