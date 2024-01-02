@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
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
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)

    implementation (libs.accompanist.permissions)

    // map
    implementation (libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.android.maps.utils)

    implementation (libs.androidx.material.icons.extended)
    implementation (libs.androidx.material)
    implementation (libs.androidx.runtime)

 //   debugImplementation(libs.compose.ui.tooling)
}

fun getApiKey(propertyKey: String):String {
    return com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir).getProperty(propertyKey)
}
