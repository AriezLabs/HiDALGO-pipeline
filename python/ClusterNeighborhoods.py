usage = """
usage: python ClusterNeighborhoods.py metisFile targetFile nthreads
metisFile = path to one indexed metis graph
targetFile = filename to write new graph with added central node connected to every other node to
"""

import sys

from networkit import *
from multiprocessing import Pool

if len(sys.argv) != 4:
    print(usage)
    sys.exit(1)

inputGraph = sys.argv[1]
outputPath = sys.argv[2]
nthreads = int(sys.argv[3])

job = [(inputGraph, x, nthreads) for x in range(nthreads)]


def task(params):
    file = params[0]
    id = params[1]
    nthreads = params[2]

    graph = readGraph(file, Format.METIS)
    for i in range(id, graph.size()[0], nthreads):
        subg = graph.subgraphFromNodes(graph.neighbors(i))


with Pool(nthreads) as p:
    p.map(task, job)

sys.exit(0)
