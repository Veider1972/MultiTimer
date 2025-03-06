import org.jetbrains.kotlin.ir.backend.js.compile

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

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
//    implementation("androidx.navigation:navigation-fragment-ktx:2.8.6")
//    implementation("androidx.navigation:navigation-ui-ktx:2.8.6")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    // Rustore review
    implementation("ru.rustore.sdk:review:7.0.0")
    // Google review
    implementation("androidx.preference:preference-ktx:1.2.1")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation(platform("androidx.compose:compose-bom:2025.01.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation:1.7.7")
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

//    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.20")

    // Koin
    implementation("io.insert-koin:koin-androidx-compose:4.0.2")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

//    implementation("com.github.antonKozyriatskyi:CircularProgressIndicator:1.3.0")
//    implementation("com.alex-zaitsev:meternumberpicker:1.0.5")

    // Room
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("android.arch.persistence.room:compiler:1.1.1")

    // Accompanist
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.01.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}