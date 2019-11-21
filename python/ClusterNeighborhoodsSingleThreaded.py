usage = """
usage: python ClusterNeighborhoods.py metisFile targetFile nthreads
metisFile = path to one indexed metis graph
targetFile = filename to write new graph with added central node connected to every other node to
"""

import sys

from networkit import *
import io
from multiprocessing import Pool

if len(sys.argv) != 4:
    print(usage)
    sys.exit(1)

inputGraph = sys.argv[1]
outputPath = sys.argv[2]
nthreads = int(sys.argv[3])


def task(params):
    file = params[0]
    id = params[1]
    nthreads = params[2]

    graph = readGraph(file, Format.METIS)

    # PLM is parallel by default, but the neighborhoods are tiny enough for 1 thread
    # also scaling completely breaks if we use too many threads
    setNumberOfThreads(1)
    for i in range(id, graph.size()[0], nthreads):
        neighbors = graph.neighbors(i)
        sub = graph.subgraphFromNodes(neighbors)
        if len(sub.edges()) > 0:
            print(sub.size())
            community.detectCommunities(sub, algo=community.PLM(sub, True, par="none"))


if __name__ == '__main__':
    task([inputGraph, 0, 1])

    sys.exit(0)
