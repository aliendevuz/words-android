import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "uz.alien.dictup"
    compileSdk = 36

    defaultConfig {
        applicationId = "uz.alien.dictup"
        minSdk = 24
        targetSdk = 36
        versionCode = 24
        versionName = "5.1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            buildConfigField("String", "BASE_URL", "\"https://assets.4000.uz/\"")
            buildConfigField("Boolean", "DISABLE_LOGGING_ON_PROD", "false")
            buildConfigField("Boolean", "LOGGING_IS_AVAILABLE", "true")
            buildConfigField("Long", "DURATION", "274L")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("String", "BASE_URL", "\"https://assets.4000.uz/\"")
            buildConfigField("Boolean", "DISABLE_LOGGING_ON_PROD", "false")
            buildConfigField("Boolean", "LOGGING_IS_AVAILABLE", "true")
            buildConfigField("Long", "DURATION", "274L")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.ui.android)
    implementation(libs.gson)
    implementation(libs.com.squareup.okhttp3)
    implementation(libs.androidx.core.animation)
    implementation(libs.coil)
    implementation(libs.integrity)
    implementation(libs.install.referrer)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.androidx.room.runtime)
    implementation(libs.android.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.hilt)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.data.store)
    implementation(libs.startup)
    implementation(libs.work)
    implementation(libs.glide)
    implementation(libs.markwon)
    implementation(libs.image.plugin)
    implementation(libs.glide.image.plugin)
    implementation(libs.konfetti)
    ksp(libs.glide)
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}