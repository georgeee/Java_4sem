#!/bin/bash

rm -Rf build

mkdir build

manifest=/tmp/MANIFEST.MF
src_files=/tmp/src_files.txt

find src -type f -name '*.java' > "$src_files"

javac -cp ../task3.jar -d build "@$src_files"

echo "Manifest-Version: 1.0" > "$manifest"

jar cvfm simple_tester.jar "$manifest" -C build/ .

rm -f "$manifest" "$src_files"
