#!/bin/sh
set -e
./gradlew clean koverXmlReportJvm
./scripts/testCoverageValidator.sh
