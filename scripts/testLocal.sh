#!/bin/sh
set -e
./gradlew clean
./gradlew jvmTest
./gradlew nativeTest
./gradlew iOSX64Test
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
