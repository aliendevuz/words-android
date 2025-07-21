plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.google.gms.google.services)
}

android {
  namespace = "uz.alien.dictup"
  compileSdk = 35

  defaultConfig {
    applicationId = "uz.alien.dictup"
    minSdk = 24
    targetSdk = 35
    versionCode = 19
    versionName = "5.0.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    viewBinding = true
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
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}