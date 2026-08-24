#!/bin/sh
set -e
./gradlew kotlinUpgradeYarnLock
./gradlew kotlinWasmUpgradeYarnLock
./scripts/ktlintCheck.sh
./scripts/test.sh
./gradlew build
./gradlew checkSigningConfiguration
./gradlew checkPomFileForKotlinMultiplatformPublication
