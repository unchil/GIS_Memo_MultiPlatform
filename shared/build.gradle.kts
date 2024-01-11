@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.skieGradle)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = false
        }
    }

    sourceSets {
        commonMain {
            dependencies {

              //  implementation(libs.gradle.plugin.loader)

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.paging.common)
                implementation(libs.paging.compose.common)


                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.android.paging3.extensions)
                implementation(libs.sqldelight.primitive.adapters)
                implementation(libs.sqldelight.coroutines.extensions)


                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)

                implementation(libs.coil)
                implementation(libs.coil.core)
                implementation(libs.coil.compose.core)
                implementation(libs.coil.network)


            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {

                implementation(libs.sqldelight.android.driver)

                implementation(libs.androidx.paging.runtime)
                implementation(libs.androidx.paging.compose)

                implementation(libs.ktor.client.okhttp)
           //     implementation(libs.ktor.client.android)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.coil.core.android)
                implementation(libs.coil.compose.core.android)
                implementation(libs.coil.network)

            //    implementation(libs.compose.material3)

            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation(libs.paging.runtime.uikit)

                implementation(libs.ktor.client.darwin)

            }
        }
    }

}


android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "com.unchil.gismemo_multiplatform"
    compileSdk = 34
    defaultConfig {
        minSdk = 33

    }
}





sqldelight {
    databases {
        create("GisMemoDatabase") {
            packageName.set("com.jetbrains.handson.kmm.shared.cache")
        }
    }
}




