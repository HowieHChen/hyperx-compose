plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "dev.lackluster.hyperx.compose"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
    }
}

@Suppress("UseTomlInstead")
dependencies {
    api("top.yukonga.miuix.kmp:miuix:0.3.5")
    api("dev.chrisbanes.haze:haze:1.6.2")
    api("androidx.compose.foundation:foundation:1.8.2")
    api("androidx.activity:activity-compose:1.10.1")
    api("androidx.navigation:navigation-compose:2.9.0")
}