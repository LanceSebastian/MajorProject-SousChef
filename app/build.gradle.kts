plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "uk.ac.aber.dcs.souschefapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "uk.ac.aber.dcs.souschefapp"
        minSdk = 35
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.datetime)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core.android)
    implementation (libs.lifecycle.livedata.ktx)  // Use the latest version
    implementation (libs.androidx.lifecycle.viewmodel.ktx.v287)  // ViewModel KTX for better LiveData handling
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.ui.test.android)
    kapt(libs.androidx.room.compiler)
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Firebase Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Authentication (optional)
    implementation("com.google.firebase:firebase-auth")

    // Firebase Storage (optional)
    implementation("com.google.firebase:firebase-storage")

    // Google Play Services Base
    implementation("com.google.android.gms:play-services-base")

    // Google ML Kit
    implementation ("com.google.mlkit:text-recognition:16.0.0")

    // ViewModel and LiveData (for MVVM)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Kotlin Coroutines (recommended for Firestore)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Splash Art
    implementation ("androidx.navigation:navigation-compose:2.7.0")
    implementation ("androidx.activity:activity-compose:1.8.0")
    implementation ("androidx.core:core-splashscreen:1.0.1")

    // Image Display
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Reorderable LazyList
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    implementation(libs.gson)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}