plugins {
    id("application")
    id("java")
    id("idea")

    // This creates a fat JAR
    id("com.gradleup.shadow") version "9.6.1"

    // Derives the version from git tags (v1.0.0, v1.0.1, ...)
    id("pl.allegro.tech.build.axion-release") version "1.21.4"

    // `./gradlew spotlessCheck` verifies formatting, `spotlessApply` fixes it
    id("com.diffplug.spotless") version "8.10.4"
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

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

// Rule-based rather than a full reformatter (google-java-format etc. reach into
// javac internals and break on new JDKs). `check`, and so `build`, runs spotlessCheck.
spotless {
    java {
        importOrder()
        removeUnusedImports()
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }
}
