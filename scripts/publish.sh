#!/bin/sh
set -e
./scripts/build.sh
./gradlew publishAllPublicationsToSonatypeRepository --max-workers 1
