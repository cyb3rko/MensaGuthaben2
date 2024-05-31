plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.cyb3rko.mensaguthaben2"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.cyb3rko.mensaguthaben2"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        resourceConfigurations.add("en")
        resourceConfigurations.add("de")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
}

if (project.hasProperty("sign")) {
    android {
        signingConfigs {
            getByName("release") {
                storeFile = file(System.getenv("KEYSTORE_FILE"))
                storePassword = System.getenv("KEYSTORE_PASSWD")
                keyAlias = System.getenv("KEYSTORE_KEY_ALIAS")
                keyPassword = System.getenv("KEYSTORE_KEY_PASSWD")
            }
        }
    }
    android.buildTypes.getByName("release").signingConfig =
        android.signingConfigs.getByName("release")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.material:material:1.12.0")
}
