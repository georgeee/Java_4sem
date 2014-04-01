#!/bin/bash

rm -Rf build

mkdir build

manifest=/tmp/MANIFEST.MF
src_files=/tmp/src_files.txt

find src -type f -name '*.java' > "$src_files"

javac -d build "@$src_files"

echo "Manifest-Version: 1.0" > "$manifest"
echo "Main-Class: ru.ifmo.ctddev.agapov.task3.Runner" >> "$manifest"

jar cvfm task3.jar "$manifest" -C build/ .

rm -f "$manifest" "$src_files"
