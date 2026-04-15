plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "CqrsLibrary"
include("lib")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")