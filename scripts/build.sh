#!/bin/sh
set -e
./scripts/ktlintCheck.sh
./scripts/test.sh
./gradlew build publishToMavenLocal
