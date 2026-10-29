plugins {
    // Lets the Java toolchain in build.gradle.kts download a JDK 21 when none is installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "gradle-hello-world"
