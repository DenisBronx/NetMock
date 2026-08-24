#!/bin/sh
set -e
./gradlew koverXmlReportCustom
./scripts/testCoverageValidator.sh
