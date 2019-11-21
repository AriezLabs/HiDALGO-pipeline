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


def task(params):
    file = params[0]
    id = params[1]
    nthreads = params[2]

    main_graph = readGraph(file, Format.METIS)

    # TODO langsam...
    def normalizedNeighborhood(g, node):
        neighbors = g.neighbors(node)
        subg = g.subgraphFromNodes(neighbors)
        copy = Graph(len(neighbors))

        idmap = dict()
        for i in range(len(neighbors)):
            idmap[neighbors[i]] = i

        subg.forEdges(lambda n0, n1, w, eid: copy.addEdge(idmap[n0], idmap[n1]))

        return copy


    # PLM is parallel by default, but the neighborhoods are tiny enough for 1 thread
    # also scaling completely breaks if we use too many threads
    setNumberOfThreads(1)

    for i in range(id, main_graph.numberOfNodes(), nthreads):

        #subg = normalizedNeighborhood(main_graph, i)
        # PLM runs slower if node ids are spread apart

        if len(subg.edges()) > 0:
            community.detectCommunities(subg, algo=community.PLM(subg, True, par="none"))


if __name__ == '__main__':
    job = [(inputGraph, x, nthreads) for x in range(nthreads)]

    with Pool(nthreads) as p:
        p.map(task, job)

    sys.exit(0)
