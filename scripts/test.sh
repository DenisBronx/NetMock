#!/bin/sh
set -e
./gradlew clean koverXmlReportCustom
./scripts/testCoverageValidator.sh
