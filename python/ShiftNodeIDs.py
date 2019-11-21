usage = """
usage: python ShiftNodeIDs.py metisFile targetFile delta
metisFile = path to one indexed metis graph
targetFile = filename to write new graph with added central node connected to every other node to
delta = integer number to add to node IDs
"""

import sys

if len(sys.argv) != 4:
    print(usage)
    sys.exit(1)

delta = int(sys.argv[3])

with open(sys.argv[1]) as graph:
    with open(sys.argv[2], "w") as outfile:
        # write header
        outfile.write(graph.readline())

        for line in graph.readlines():
            for id in line.split():
                outfile.write(str(int(id) + delta) + " ")
            outfile.write("\n")

sys.exit(0)
