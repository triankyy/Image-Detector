/*
 * Created by kyy on 3/23/23, 12:05 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 3/22/23, 11:43 PM
 */

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.triankyy.imagedetector"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.triankyy.imagedetector"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled  = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val navVersion = "2.5.3"
    val composeUiVersion = rootProject.extra["compose_ui_version"]

    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.ui:ui:${composeUiVersion}")
    implementation("androidx.compose.ui:ui-tooling-preview:${composeUiVersion}")
    implementation("androidx.compose.material3:material3:1.1.0-alpha08")
    implementation("org.tensorflow:tensorflow-lite:+")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${composeUiVersion}")
    debugImplementation("androidx.compose.ui:ui-tooling:${composeUiVersion}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${composeUiVersion}")
}