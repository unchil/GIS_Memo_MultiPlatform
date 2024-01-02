enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
      //  maven (   url ="https://androidx.dev/storage/compose-compiler/repository/"   )
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
      //  maven (  url ="https://androidx.dev/storage/compose-compiler/repository/" )
    }
}

rootProject.name = "GIS_Memo_MultiPlatform"
include(":androidApp")
include(":shared")