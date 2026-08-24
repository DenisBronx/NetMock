#!/bin/sh
set -e
./gradlew kotlinUpgradeYarnLock
./scripts/ktlintCheck.sh
./scripts/test.sh
./gradlew build
./gradlew checkSigningConfiguration
./gradlew checkPomFileForKotlinMultiplatformPublication
