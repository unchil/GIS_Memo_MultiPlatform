enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven (   url ="https://github.com/touchlab/SKIE/releases"   )
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven (   url ="https://github.com/touchlab/SKIE/releases"   )
    }
}

rootProject.name = "GIS_Memo_MultiPlatform"
include(":androidApp")
include(":shared")