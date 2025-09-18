#!/bin/bash

# Run script for the Tiger-IR optimizer
# This script takes 1 command line argument:
# $1 - path to the input IR file

# Check if correct number of arguments provided
if [ $# -ne 1 ]; then
    echo "Usage: $0 <input_ir_file>"
    exit 1
fi

# Run the optimizer
java -cp ./build Main "$1" "out.ir"
