plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.veider.multitimer"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.veider.multitimer"
        minSdk =26
        targetSdk =34
        // Не забыть обновить about_date
        versionCode = 26
        versionName = "1.3.0"

//        setProperty("archivesName", "multitimer-${versionName}-${versionCode}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.core.ktx)
    implementation(libs.appCompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.work.runtime.ktx)
    implementation(libs.lifecycle.service)
    // Rustore review
    implementation(libs.rustore.sdk)
    // Google review
    implementation(libs.preference.ktx)
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(platform(libs.compose.bom))
    implementation(libs.composereorderable)

    // Koin
    implementation(libs.koin.compose.ktx)

    // Gson
    implementation(libs.gson)

    // Room
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    ksp(libs.arch.room.compiler)

    // Accompanist
    implementation(libs.accompanist)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.junit.ext)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidTest.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.test.manifest)

}