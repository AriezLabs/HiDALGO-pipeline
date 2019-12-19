usage = """
Read Metis file from FIFO passed via argv[1]
Cluster it using argv[2] threads, append communities larger than argv[4] to argv[5]
Write 'k' to argv[3] FIFO as response and wait for next metis file to appear in argv[1]
Terminate when "END" is written to argv[1]
"""

from networkit import *
import sys

if len(sys.argv) != 6:
    print(usage)
    sys.exit(1)

setNumberOfThreads(int(sys.argv[3]))
communityMinSize = int(sys.argv[4])
outfile = sys.argv[5]

with open(outfile, "w") as f:
    f.write("")

while True:
    idmap = {}

    with open(sys.argv[1], "r") as f:
        head = f.readline()
        if head.strip() == "END":
            break
        lines = f.readlines()

    with open(sys.argv[2], "w") as f:
        f.write("k")

    cnode = int(head.split()[2]) + 1
    g = Graph(int(head.split()[0]))
    i = 0
    for line in lines:
        split = line.strip().split()
        idmap[i] = split[0]
        for x in split[1:]:
            if not g.hasEdge(int(x) - 1, i):
                g.addEdge(i, int(x) - 1)
        i += 1

    c = community.detectCommunities(g, algo=community.PLM(g, True))

    with open(outfile, "a") as f:
        for subset in c.getSubsetIds():
            m = c.getMembers(subset)
            if len(m) + 1 >= communityMinSize: # +1: central node
                f.write(str(cnode) + " ")
                for node in m:
                    f.write(idmap[node] + " ")
                f.write("\n")
