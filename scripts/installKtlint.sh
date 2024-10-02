#!/bin/sh
set -e
curl -sSLO https://github.com/pinterest/ktlint/releases/download/1.3.1/ktlint && chmod a+x ktlint && sudo mv ktlint /usr/local/bin/
