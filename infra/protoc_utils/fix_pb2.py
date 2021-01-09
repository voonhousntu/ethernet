#!/usr/bin/python3

import fileinput
import glob
import re
import sys


def fix_pb2_grpc_file(file_path: str):
    # Use fileinput as it supports inplace editing
    # It (fileinput) redirects stdout to the file
    pattern = re.compile("^import .*_pb2$")
    with fileinput.FileInput(file_path, inplace=True) as f:
        for line in f:
            if pattern.match(line):
                line = "from . " + line

            # Do not want new lines
            print(line, end="")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise Exception("Path to *_pb2.py files not provided")

    files_to_fix = glob.glob(sys.argv[1])
    for file_path in files_to_fix:
        if "_pb2_grpc.py" in file_path:
            fix_pb2_grpc_file(file_path)
