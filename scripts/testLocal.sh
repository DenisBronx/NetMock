#!/bin/sh
set -e
./gradlew jvmTest
./gradlew nativeTest
./gradlew iOSX64Test
./gradlew jsTest
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
