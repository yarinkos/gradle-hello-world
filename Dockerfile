# syntax=docker/dockerfile:1

# ─── build ────────────────────────────────────────────────────────────────────
# Compiles, runs the format check and unit tests, and packages the fat jar.
# There is no .git in the build context, so axion cannot read the version from
# tags; CI computes it once and passes it in as VERSION.
FROM eclipse-temurin:21-jdk AS build
ARG VERSION
RUN test -n "$VERSION" || (echo "VERSION build arg is required, e.g. --build-arg VERSION=1.0.1" >&2 && exit 1)

WORKDIR /src

# Wrapper and build scripts first: this layer (Gradle download) stays cached
# until they change, instead of on every source edit.
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon --version

COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon build -PappVersion="$VERSION" \
    && cp "build/libs/gradle-hello-world-${VERSION}-all.jar" /app.jar

# ─── runtime ──────────────────────────────────────────────────────────────────
# JRE only: no JDK, Gradle or sources in the final image.
FROM eclipse-temurin:21-jre-alpine
ARG VERSION

LABEL org.opencontainers.image.title="gradle-hello-world" \
      org.opencontainers.image.version="${VERSION}" \
      org.opencontainers.image.source="https://github.com/yarinkos/gradle-hello-world"

# Fixed, unprivileged UID/GID so the user is the same on every build.
RUN addgroup -S -g 10001 app && adduser -S -D -H -u 10001 -G app app

WORKDIR /app
# Owned by root and read-only for the app user: the process cannot modify its own jar.
COPY --from=build /app.jar /app/app.jar

USER 10001:10001
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
