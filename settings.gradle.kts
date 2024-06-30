pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}


dependencyResolutionManagement {
//    versionCatalogs {
//        create("libs") {
//            from(files("libs.versions.toml"))
//        }
//    }
}

rootProject.name = "ImageProcess"
