#!/bin/sh
set -e
./gradlew clean koverXmlReport
./scripts/testCoverageValidator.sh
