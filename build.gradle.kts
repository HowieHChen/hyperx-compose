plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
}

android {
    namespace = "dev.lackluster.hyperx.compose"
    compileSdk = 36

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
    kotlin {
        jvmToolchain(21)
    }
}

@Suppress("UseTomlInstead")
dependencies {
    api("top.yukonga.miuix.kmp:miuix:0.5.1")
    api("dev.chrisbanes.haze:haze:1.6.10")
    api("androidx.compose.foundation:foundation:1.9.0")
    api("androidx.activity:activity-compose:1.10.1")
    api("androidx.navigation:navigation-compose:2.9.3")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.3")
    implementation("io.github.biezhi:TinyPinyin:2.0.3.RELEASE")
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
}