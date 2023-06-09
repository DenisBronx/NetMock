#!/bin/sh
set -e
./scripts/ktlintCheck.sh
./gradlew clean build publishToMavenLocal
