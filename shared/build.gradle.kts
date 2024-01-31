@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
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

                implementation(libs.kotlinx.coroutines.core)

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

                implementation("co.touchlab:stately-common:2.0.6")
                implementation("co.touchlab:stately-isolate:2.0.6")
                implementation("co.touchlab:stately-iso-collections:2.0.6")

                implementation(libs.paging.common)
                implementation(libs.paging.compose.common)


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

                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.coil.core.android)
                implementation(libs.coil.compose.core.android)
                implementation(libs.coil.network)

            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.native.driver)
                implementation(libs.paging.runtime.uikit)
                implementation(libs.ktor.client.darwin)

                implementation(libs.androidx.recyclerview)

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




