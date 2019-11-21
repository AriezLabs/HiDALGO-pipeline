# Pipeline Stages

## CutNeighborhoods

* Takes MetisFile
* Returns ???
* Params: none
* Semantics: Create a separate graph out of the immediate neighborhood of each node

## ClusterNeighborhoods

* Takes ???
* Returns ???
* Params:
    * Threads: int, number of threads to use
* Semantics: Cluster neighborhoods using KIT's implementation of PLM