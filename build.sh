#!/bin/bash

# Build script for the Tiger-IR optimizer
# This script compiles all Java source files for the optimizer

# Create build directory if it doesn't exist
mkdir -p build

# Generate list of all Java source files
find materials/src -name "*.java" > sources.txt

# Compile all Java files
javac -d build @sources.txt

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Build successful!"
else
    echo "Build failed!"
    exit 1
fi

# Clean up temporary files
rm sources.txt