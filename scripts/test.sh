#!/bin/sh
set -e
./gradlew jsTest
./gradlew wasmJsTest
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
