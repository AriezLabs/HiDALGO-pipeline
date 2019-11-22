package process;

import excption.BadConfigException;
import log.Log;
import type.DataType;
import type.filetype.MetisFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.util.*;

public class CutNeighborhoods implements Stage {

    Neighborhood[] neighborhoods;
    HashMap<Integer, Neighborhood>[] index;

    @Override
    public Class getInputType() {
        return MetisFile.class;
    }

    @Override
    public Class getReturnType() {
        return null;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {

    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile mf = (MetisFile) uncastedInput;

        firstPass(mf);
        Log.info("inited neighborhoods and indexed", 1);
        secondPass(mf);
        Log.info("extracted neighborhoods", 1);
        // TODO write writer; check correctness of this beast against frameworks etc.
        write();

        return null;
    }

    private void write() {

    }

    /**
     * create and init neighborhood class instances, construct index
     * @return success
     */
    private void firstPass(MetisFile input) throws Exception {
        File in = input.getFile();

        try (BufferedReader br = new BufferedReader(new FileReader(in))) {
            StreamTokenizer st = new StreamTokenizer(br);
            st.eolIsSignificant(true);

            String line = br.readLine();

            String[] header = line.split(" ");
            if (header.length > 2)
                Log.log(String.format("metis file %s: ignoring header info beyond nnodes and nedges", in.getName()), Log.WARNING, 1);

            int numberOfNodes = Integer.parseInt(header[0]);
            int numberOfEdges = Integer.parseInt(header[1]);
            int averageDegree = numberOfEdges / numberOfNodes;

            neighborhoods = new Neighborhood[numberOfNodes];
            index = new HashMap[numberOfNodes];

            // initialize array lists
            for (int i = 0; i < neighborhoods.length; i++) {
                neighborhoods[i] = new Neighborhood();
                index[i] = new HashMap<>(2 * numberOfEdges / numberOfNodes);
            }

            int currentLine = 0;
            int currentNode;
            int neighborsCount = 0;
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                // on eol, initiate neighborhood because only now do we know the number of nodes in it
                if (st.ttype == StreamTokenizer.TT_EOL) {
                    neighborhoods[currentLine].init(neighborsCount, 2 * averageDegree, currentLine);
                    neighborsCount = 0;
                    currentLine++;

                } else {
                    // when we see a number we note that the current neighborhood contains the node with the corresponding id
                    // and map the id to a continuous one within the neighborhood.
                    neighborsCount++;
                    currentNode = ((int) st.nval) - input.getIndexOffset();
                    neighborhoods[currentLine].mapId(currentNode);
                    index[currentNode].put(currentLine, neighborhoods[currentLine]);
                }
            }
        }
    }

    private void secondPass(MetisFile input) throws Exception {
        File in = input.getFile();

        try (BufferedReader br = new BufferedReader(new FileReader(in))) {
            StreamTokenizer st = new StreamTokenizer(br);
            st.eolIsSignificant(true);

            // skip header
            br.readLine();

            int fromNode = 0;
            int toNode;
            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                if (st.ttype == StreamTokenizer.TT_EOL) {
                    fromNode++;

                } else {
                    toNode = ((int) st.nval) - input.getIndexOffset();

                    // For all neighborhoods that contain nodes fromNode and toNode,
                    // add an edge between the two nodes
                    // choose the smaller of both maps to iterate over, shaves a few % off runtime
                    for(Neighborhood n : index[fromNode].values())
                        if (index[toNode].containsKey(n.centralNode)) {
                            n.addEdgeMapped(fromNode, toNode);
                        }
                }
            }
        }
    }

    private class Neighborhood {
        ArrayList<Integer>[] adjacencyList;
        int centralNode;

        HashMap<Integer, Integer> idMap = new HashMap<>();
        int currentMaxId = 0;

        public void init(int n, int initialCapacity, int centralNode) {
            this.centralNode = centralNode;
            mapId(centralNode);
            adjacencyList = new ArrayList[n];
            for (int i = 0; i < n; i++) {
                adjacencyList[i] = new ArrayList<>(initialCapacity);
            }
        }

        public void addEdgeMapped(int fromNode, int toNode) {
            adjacencyList[idMap.get(fromNode)].add(idMap.get(toNode));
        }

        public void mapId(int old) {
            idMap.put(old, currentMaxId);
            currentMaxId += 1;
        }
    }
}
