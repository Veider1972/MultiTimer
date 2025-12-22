import org.gradle.cache.internal.ProducerGuard.adaptive
import org.gradle.internal.impldep.org.joda.time.format.DateTimeFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.compose")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "ru.veider.multitimer"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.veider.multitimer"
        minSdk =29
        targetSdk =34
        // Не забыть обновить about_date
        versionCode = 27
        versionName = "1.4.0"

//        setProperty("archivesName", "multitimer-${versionName}-${versionCode}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BUILD_DATE", "\"${DateTimeFormatter.ofPattern("LLLL, yyyy").format(LocalDateTime.now())}\"")
            buildConfigField("String", "BUILD_YEAR", "\"${LocalDateTime.now().year}\"")
        }
        release {
            buildConfigField("String", "BUILD_DATE", "\"${DateTimeFormatter.ofPattern("LLLL, yyyy").format(LocalDateTime.now())}\"")
            buildConfigField("String", "BUILD_YEAR", "\"${LocalDateTime.now().year}\"")
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
    implementation(libs.core.ktx)
    implementation(libs.core)
    implementation(libs.appCompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.work.runtime.ktx)
    implementation(libs.lifecycle.service)
    // RuStore review
    implementation(libs.rustore.sdk)
    // Google review
    implementation(libs.preference.ktx)
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.animation)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(platform(libs.compose.bom))
    implementation(libs.composereorderable)

    // Navigation
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.ui)
    implementation(libs.navigation.router)
    implementation(libs.navigation.material3.adaptive)

    // Serialization
    implementation(libs.serialization.core)

    // Koin
    implementation(libs.koin.compose.ktx)

    // Gson
    implementation(libs.gson)

    // Room
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.gradle.plugin)
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