@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.secretsGradle)
}

android {
    namespace = "com.unchil.gismemo_multiplatform.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.unchil.gismemo_multiplatform.android"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "MAPS_API_KEY", getApiKey("MAPS_API_KEY"))
        buildConfigField("String", "OPENWEATHER_KEY", getApiKey("OPENWEATHER_KEY"))
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation (libs.androidx.material3.window.size)
    implementation(libs.androidx.activity.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.material)
    implementation (libs.androidx.runtime)
    implementation(libs.compose.foundation)
    implementation (libs.androidx.foundation.layout)
    implementation (libs.androidx.animation)



    implementation (libs.accompanist.permissions)

  //  implementation(libs.kotlinx.coroutines.android)
  //  implementation(libs.kotlinx.coroutines.android)

    // map
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.android.maps.utils)
    // map compose
    implementation (libs.maps.compose)
    // Optionally, you can include the Compose utils library for Clustering, etc.
    implementation (libs.maps.compose.utils)
    // Optionally, you can include the widgets library for ScaleBar, etc.
    implementation (libs.maps.compose.widgets)



    // Camera
    implementation (libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)

    //Navigation
    implementation (libs.androidx.navigation.compose)
    implementation (libs.accompanist.navigation.animation)


    // coil2
    //implementation (libs.coil.compose)

    // coil3     implementation(libs.ktor.client.okhttp)
    implementation(libs.coil.android)
    implementation(libs.coil.core.android)
    implementation(libs.coil.compose.core.android)
    implementation(libs.coil.network)


    // ktor
    implementation(libs.ktor.client.okhttp)


    // media3 formally exo Player
    implementation (libs.androidx.media3.exoplayer)
    implementation (libs.androidx.media3.exoplayer.dash)
    implementation (libs.androidx.media3.ui)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.android)

 //   debugImplementation(libs.compose.ui.tooling)
    
    //Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.rxjava3)


    implementation(libs.androidx.biometric)

}

fun getApiKey(propertyKey: String):String {
    return com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty(propertyKey)
}
