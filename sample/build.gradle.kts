plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fakhrirasyids.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fakhrirasyids.sample"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.3"
    }
}

dependencies {

    implementation(project(":Highlightor"))
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0")
    implementation("androidx.activity:activity-compose:1.5.0")
    implementation("androidx.compose.ui:ui:1.0.3")
    implementation("androidx.compose.ui:ui-graphics:1.0.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.0.3")
    implementation("androidx.compose.material:material:1.0.3")
    implementation ("androidx.compose.runtime:runtime:1.0.3")
    implementation ("androidx.compose.compiler:compiler:1.0.3")
}