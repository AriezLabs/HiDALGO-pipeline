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
    ArrayList<Neighborhood>[] index;

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
        Log.info("inited db and indexed", 1);
        secondPass(mf);
        Log.info("filled db", 1);
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
            index = new ArrayList[numberOfNodes];

            // initialize array lists
            for (int i = 0; i < neighborhoods.length; i++) {
                neighborhoods[i] = new Neighborhood();
                index[i] = new ArrayList<>(2 * numberOfEdges / numberOfNodes);
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
                    neighborsCount++;
                    currentNode = ((int) st.nval) - input.getIndexOffset();
                    index[currentNode].add(neighborhoods[currentLine]);
                }
            }

            for(ArrayList<Neighborhood> al : index)
                Collections.sort(al);
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
                    // TODO use sorting instead of naive n^2
                    for(Neighborhood n1 : index[fromNode])
                        for(Neighborhood n2 : index[toNode])
                            if(n1 == n2)
                                // TODO id map is missing
                                n1.addEdge(fromNode, toNode);
                }
            }
        }
    }

    private class Neighborhood implements Comparable<Neighborhood> {
        ArrayList<Integer>[] adjacencyList;
        int centralNode;

        public void init(int n, int initialCapacity, int centralNode) {
            this.centralNode = centralNode;
            adjacencyList = new ArrayList[n];
            for (int i = 0; i < n; i++) {
                adjacencyList[i] = new ArrayList<>(initialCapacity);
            }
        }

        @Override
        public int compareTo(Neighborhood o) {
            return this.centralNode - o.centralNode;
        }

        public void addEdge(int fromNode, int toNode) {
            adjacencyList[fromNode].add(toNode);
        }
    }
}
