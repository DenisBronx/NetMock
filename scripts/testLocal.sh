#!/bin/sh
set -e
./gradlew kotlinUpgradeYarnLock
./gradlew kotlinWasmUpgradeYarnLock
./gradlew jvmTest
./gradlew nativeTest
./gradlew iOSX64Test
./gradlew jsTest
./gradlew wasmJsTest
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
