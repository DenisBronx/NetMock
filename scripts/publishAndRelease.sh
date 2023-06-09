#!/bin/sh
set -e
./scripts/publish.sh
./gradlew closeAndReleaseStagingRepository --max-workers 1
