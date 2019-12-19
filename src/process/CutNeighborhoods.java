package process;

import excption.BadConfigException;
import log.Log;
import type.DataType;
import type.Neighborhoods;
import type.filetype.MetisFile;

import java.io.*;
import java.util.*;

/**
 * Cut out all neighborhoods. Do not include their central node
 * Return Neighborhoods object, a wrapper around an array of neighborhoods
 */
public class CutNeighborhoods implements Stage {

    Neighborhoods.Neighborhood[] neighborhoods;
    HashMap<Integer, Neighborhoods.Neighborhood>[] index;

    @Override
    public Class getInputType() {
        return MetisFile.class;
    }

    @Override
    public Class getReturnType() {
        return Neighborhoods.class;
    }

    @Override
    public void configure(Map<String, String> params) throws BadConfigException {
    }

    @Override
    public DataType execute(DataType uncastedInput) throws Exception {
        MetisFile mf = (MetisFile) uncastedInput;

        firstPass(mf);
        Log.info("inited and indexed neighborhoods", 1);

        secondPass(mf);
        Log.info("extracted neighborhoods", 1);

        return new Neighborhoods(neighborhoods, mf.getFile().getPath());
    }

    /**
     * create and init neighborhood class instances, construct index
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

            neighborhoods = new Neighborhoods.Neighborhood[numberOfNodes];
            index = new HashMap[numberOfNodes];

            // initialize array lists
            for (int i = 0; i < neighborhoods.length; i++) {
                neighborhoods[i] = new Neighborhoods.Neighborhood();
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

    /**
     * this step fills the neighborhood objects with all edges belonging to them
     */
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
                    for(Neighborhoods.Neighborhood n : index[fromNode].values())
                        if (index[toNode].containsKey(n.getCentralNode())) {
                            n.addEdgeMapped(fromNode, toNode);
                        }
                }
            }
        }
    }

}
