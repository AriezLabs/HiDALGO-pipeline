# Test output from CutNeighborhoods against NetworKit
# Takes as arguments:
# <path to CutNeighborhoods output> <path to original graph>

from networkit import *
import sys

g = readGraph(sys.argv[2], Format.METIS)

with open(sys.argv[1]) as f:
    node = 0
    head = []
    lines = []

    def nextg():
        global node
        global head
        global lines

        node = int(f.readline().strip())
        head = f.readline().strip().split()
        lines = []
        for i in range(int(head[0])):
            lines.append(f.readline())

    def writeg(node, head, lines):
        with open("tmp_cntest", "w") as f:
            f.write(head[0] + " " + head[1] + "\n")
            for line in lines:
                f.write(line)

    while True:
        nextg()
        writeg(node, head, lines)
        cng = readGraph("tmp_cntest", Format.METIS)
        nkg = g.subgraphFromNodes(g.neighbors(node))
        nkg = graph.GraphTools.getCompactedGraph(nkg, graph.GraphTools.getContinuousNodeIds(nkg))
        equalList = []
        cng.forEdges(lambda a,b,w,i: equalList.append(nkg.hasEdge(a,b)))
        nkg.forEdges(lambda a,b,w,i: equalList.append(cng.hasEdge(a,b)))
        print(all(equalList))
        f.readline()
