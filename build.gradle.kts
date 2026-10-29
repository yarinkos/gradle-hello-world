plugins {
    id("application")
    id("java")
    id("idea")

    // This creates a fat JAR
    id("com.gradleup.shadow") version "9.6.1"

    // Derives the version from git tags (v1.0.0, v1.0.1, ...)
    id("pl.allegro.tech.build.axion-release") version "1.21.4"
}

group = "com.ido"
description = "HelloWorld"

scmVersion {
    tag {
        // The version used before the first tag exists (exercise task 4b).
        initialVersion { _, _ -> "1.0.0" }
    }
}

// The version normally comes from the latest git tag. CI passes -PappVersion=X.Y.Z
// to build an exact version where there is no git history, e.g. inside Docker.
version = providers.gradleProperty("appVersion").getOrElse(scmVersion.version)

// Compile and run on Java 21 regardless of the JDK Gradle itself runs on.
// If 21 is not installed locally, the foojay resolver (settings.gradle.kts) downloads it.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application.mainClass.set("com.ido.HelloWorld")

// Stamp the version into META-INF/MANIFEST.MF (shadowJar inherits this manifest).
tasks.jar {
    manifest {
        attributes("Implementation-Version" to project.version)
    }
}

repositories {
    mavenCentral()
}
