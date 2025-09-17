#!/bin/bash

# Run script for the Tiger-IR optimizer
# This script takes 2 command line arguments:
# $1 - path to the input IR file
# $2 - path where the output IR file should be created

# Check if correct number of arguments provided
if [ $# -ne 2 ]; then
    echo "Usage: $0 <input_ir_file> <output_ir_file>"
    exit 1
fi

# Run the optimizer
java -cp ./build Main "$1" "$2"
