#!/bin/sh
set -e
./scripts/ktlintCheck.sh
./scripts/test.sh
./gradlew build
./gradlew checkSigningConfiguration
./gradlew checkPomFileForKotlinMultiplatformPublication
