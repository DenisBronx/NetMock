#!/bin/sh
set -e
curl -sSLO https://github.com/pinterest/ktlint/releases/download/0.49.1/ktlint && chmod a+x ktlint && sudo mv ktlint /usr/local/bin/
