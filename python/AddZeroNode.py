usage = """
usage: python AddZeroNode.py metisFile targetFile
metisFile = path to one indexed metis graph
targetFile = filename to write new graph with added central node connected to every other node to
"""

import sys

if len(sys.argv) != 3:
    print(usage)
    sys.exit(1)

with open(sys.argv[1]) as graph:
    with open(sys.argv[2], "w") as outfile:
        header = [int(x) for x in graph.readline().split()]

        # update header
        outfile.write("{0} {1}\n".format(header[0] + 1, header[1] + header[0]))

        # write new central node
        for i in range(2, header[0] + 2):
            outfile.write(str(i) + " ")

        # for each node
        for line in graph.readlines():
            # add central node as neighbor
            outfile.write("\n1 ")

            # write other neighbors shifted up by 1
            for node in line.split():
                outfile.write(str(int(node) + 1) + " ")

sys.exit(0)
