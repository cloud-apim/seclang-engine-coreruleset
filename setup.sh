#!/bin/sh

set -e

VERSION=${1:-v4.29.0}
CRS_VERSION=${VERSION#v}

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
TARGET_DIR="$SCRIPT_DIR/src/main/resources/crs"
TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

echo "Downloading CRS version $VERSION..."
wget -q "https://github.com/coreruleset/coreruleset/archive/${VERSION}.zip" -O "$TMP_DIR/crs.zip"

echo "Extracting archive..."
unzip -q "$TMP_DIR/crs.zip" -d "$TMP_DIR"

EXTRACTED_DIR=$(ls -d "$TMP_DIR"/coreruleset-* | head -1)

echo "Cleaning target directory..."
# everything below is generated from one CRS version: nothing may survive from a previous run
rm -rf "$TARGET_DIR/rules" "$TARGET_DIR/crs-setup.conf" "$TARGET_DIR/crs-setup.conf.example"
mkdir -p "$TARGET_DIR"

echo "Copying rules and configuration..."
cp -r "$EXTRACTED_DIR/rules" "$TARGET_DIR/"
# the preset loads every .conf file of the directory, so exactly one setup file is shipped:
# the example of the same version, installed the way the CRS documentation says to
cp "$EXTRACTED_DIR/crs-setup.conf.example" "$TARGET_DIR/crs-setup.conf"

echo "Checking versions..."
FOUND=$(grep -rhoE "ver:'OWASP_CRS/[0-9]+\.[0-9]+\.[0-9]+'" "$TARGET_DIR" | sort -u)
if [ "$FOUND" != "ver:'OWASP_CRS/$CRS_VERSION'" ]; then
  echo "Expected only CRS $CRS_VERSION in $TARGET_DIR, found:" >&2
  echo "$FOUND" >&2
  exit 1
fi

echo "Done! CRS $VERSION installed in $TARGET_DIR"
