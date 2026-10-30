# Local shortcuts for the same steps CI runs. `make` or `make help` lists them.
# Works with the GNU Make 3.81 that ships with macOS.

IMAGE ?= yarinkos/gradle-hello-world
EXPECTED_OUTPUT := Hello World from Yarin!

# The version comes from git tags via axion, exactly as in CI, e.g.
# 1.0.3 on a released commit, 1.0.4-build-makefile-SNAPSHOT on a branch.
# Computed lazily and only once (Gradle takes a few seconds); override it with
# `make docker-build VERSION=1.2.3`.
VERSION ?= $(eval VERSION := $$(shell ./gradlew -q currentVersion -Prelease.quiet))$(VERSION)

JAR = build/libs/gradle-hello-world-$(VERSION)-all.jar

.DEFAULT_GOAL := help
.PHONY: help version build test format format-check run-jar docker-build run pull-run verify clean

help: ## List the targets
	@grep -E '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk -F':.*## ' '{printf "  make %-14s %s\n", $$1, $$2}'

version: ## Print the version axion derives from git
	@echo $(VERSION)

build: ## Format check, compile, unit tests and the fat jar
	./gradlew build -PappVersion=$(VERSION)

test: ## Unit tests only
	./gradlew test

format: ## Fix formatting (Spotless)
	./gradlew spotlessApply

format-check: ## Check formatting, as CI does
	./gradlew spotlessCheck

run-jar: build ## Build and run the jar
	java -jar $(JAR)

docker-build: ## Build the image, tagged with the current version
	docker build --build-arg VERSION=$(VERSION) -t $(IMAGE):$(VERSION) .

run: docker-build ## Build and run the image locally
	docker run --rm $(IMAGE):$(VERSION)

pull-run: ## Pull the latest published image from Docker Hub and run it
	docker run --rm --pull always $(IMAGE):latest

verify: docker-build ## Build the image and check its output and non-root user, like CI
	@OUTPUT="$$(docker run --rm $(IMAGE):$(VERSION))"; \
	  echo "$$OUTPUT"; \
	  [ "$$OUTPUT" = "$(EXPECTED_OUTPUT)" ] || { echo "unexpected output" >&2; exit 1; }
	@USER_ID="$$(docker run --rm --entrypoint id $(IMAGE):$(VERSION) -u)"; \
	  echo "uid=$$USER_ID"; \
	  [ "$$USER_ID" != "0" ] || { echo "image runs as root" >&2; exit 1; }

clean: ## Remove build output
	./gradlew clean
