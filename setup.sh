#!/bin/sh

set -e

VERSION=${1:-v4.22.0}

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
TARGET_DIR="$SCRIPT_DIR/src/main/resources/crs"
TMP_DIR=$(mktemp -d)

echo "Downloading CRS version $VERSION..."
wget -q "https://github.com/coreruleset/coreruleset/archive/${VERSION}.zip" -O "$TMP_DIR/crs.zip"

echo "Extracting archive..."
unzip -q "$TMP_DIR/crs.zip" -d "$TMP_DIR"

EXTRACTED_DIR=$(ls -d "$TMP_DIR"/coreruleset-* | head -1)

echo "Cleaning target directory..."
rm -rf "$TARGET_DIR/rules" "$TARGET_DIR/crs-setup.conf.example"
mkdir -p "$TARGET_DIR"

echo "Copying rules and configuration..."
cp -r "$EXTRACTED_DIR/rules" "$TARGET_DIR/"
cp "$EXTRACTED_DIR/crs-setup.conf.example" "$TARGET_DIR/"

echo "Cleaning up..."
rm -rf "$TMP_DIR"

echo "Done! CRS $VERSION installed in $TARGET_DIR"
