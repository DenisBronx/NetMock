#!/bin/sh
set -e
ktlint "**/*.kt" "!**/generated/**" "!**/build/**" --code-style=android_studio --color --color-name=RED
