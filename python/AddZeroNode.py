# Input: One indexed metis graph
# Output: Overrides graph, adding zero node connected to every other node to the graph.
# exit code is printed to stdout
import sys
from networkit import *

g = readGraph(sys.argv[1], Format.METIS)
print(g)

sys.exit(0)
