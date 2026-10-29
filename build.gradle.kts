plugins {
    id("application")
    id("java")
    id("idea")

    // This creates a fat JAR
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.ido"
description = "HelloWorld"

// Compile and run on Java 21 regardless of the JDK Gradle itself runs on.
// If 21 is not installed locally, the foojay resolver (settings.gradle.kts) downloads it.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application.mainClass.set("com.ido.HelloWorld")

repositories {
    mavenCentral()
}
