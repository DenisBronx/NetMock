#!/bin/sh
set -e
./gradlew test
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
