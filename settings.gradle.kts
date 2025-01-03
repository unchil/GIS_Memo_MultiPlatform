enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven {
            url = uri("https://androidx.dev/storage/compose-compiler/repository/")
        }

    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
 //   repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://androidx.dev/storage/compose-compiler/repository/")
        }

    }
}

rootProject.name = "GIS_Memo_MultiPlatform"
include("androidApp")
include("shared")
