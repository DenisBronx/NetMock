#!/bin/sh
set -e
ktlint "**/*.kt" "!**/generated/**" "!**/build/**" -F --code-style=android_studio --color --color-name=RED
