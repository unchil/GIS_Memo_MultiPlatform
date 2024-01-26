@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinAndroid).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
    alias(libs.plugins.sqldelight).apply(false)
    alias(libs.plugins.kotlinSerialization).apply(false)
    alias(libs.plugins.secretsGradle).apply(false)
}



tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}