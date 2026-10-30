# Understanding the repository

Answers to task 2 of the exercise. Back to the [README](../README.md).

**Which programming language is this?**
Java. The build script, `build.gradle.kts`, is written in Kotlin, but there is no
Kotlin application code.

**What is Gradle?**
A build automation tool for the JVM. It compiles code, resolves dependencies from
repositories such as Maven Central, runs tests and packages artifacts. Builds are
described in code (Kotlin or Groovy DSL) rather than XML, and plugins add
capabilities (here: `application`, `shadow` for the fat jar, `axion-release` for
versioning, `spotless` for formatting).

**How does Gradle work?**
A build runs in three phases:
1. **Initialization**: reads `settings.gradle.kts` to find the projects in the build.
2. **Configuration**: runs the build scripts and builds a graph of tasks and their
   dependencies (for example `build` → `check` → `test` → `compileJava`).
3. **Execution**: runs only the tasks the requested task needs, in dependency order.

Each task declares its inputs and outputs, so an unchanged task is skipped
(`UP-TO-DATE`) or restored from the build cache. That is why a second build is
much faster. A background daemon keeps a warm JVM between builds.

**What is the Gradle wrapper?**
`gradlew` / `gradlew.bat` plus `gradle/wrapper/`. It is a small launcher committed
to the repository that downloads the exact Gradle version named in
`gradle-wrapper.properties` and runs it. Nobody has to install Gradle, and every
developer and CI machine builds with the same version. Here it is also pinned with
a SHA-256 of the distribution, so a tampered download is rejected.
