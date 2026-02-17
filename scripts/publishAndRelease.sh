#!/bin/sh
set -e
./scripts/build.sh
./gradlew publishToMavenCentral --no-configuration-cache
