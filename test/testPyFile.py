from networkit import *
import sys

setNumberOfThreads(int(sys.argv[3]))

while True:
    head = []
    lines = []

    with open("/tmp/test", "r") as f:
        head = f.readline()
        if head.strip() == "END":
            break
        lines = f.readlines()

    with open("/tmp/test2", "w") as f:
        f.write("k")

    g = Graph(int(head.split()[0]))
    i = 0
    for line in lines:
        for x in line.strip().split():
            if not g.hasEdge(int(x) - 1, i):
                g.addEdge(i,int(x) - 1)
        i += 1
    c = community.detectCommunities(g, algo=community.PLM(g, True))
