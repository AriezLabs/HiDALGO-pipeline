package type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Wrapper around Neighborhood class that also features a writer
 */
public class Neighborhoods implements DataType {
    Neighborhood[] neighborhoods;
    String parentGraph;

    /**
     * Removes empty neighborhoods
     * @param neighborhoods
     * @param path
     */
    public Neighborhoods(Neighborhood[] neighborhoods, String path) {
        ArrayList<Neighborhood> al = new ArrayList<>(neighborhoods.length);
        for(Neighborhood n : neighborhoods)
            if(n.numEdges() > 0)
                al.add(n);
        this.neighborhoods = al.toArray(new Neighborhood[0]);
        this.parentGraph = path;
    }

    public int getNumNeighborhoods() {
        return neighborhoods.length;
    }

    public String getParentGraph() {
        return parentGraph;
    }

    /**
     * write a single one indexed neighborhood to a (named pipe) file
     */
    public void toPipe(String path, int i) throws IOException, InterruptedException {
        FileWriter fw = new FileWriter(new File(path));
        Neighborhood n = neighborhoods[i];

        fw.write(n.numNodes() + " " + n.numEdges() / 2 + " " + n.centralNode + "\n");

        for (int j = 0; j < n.adjacencyList.length; j++) {
            fw.write((n.reverseIdMap.get(j) + 1) + " ");
            for (int k : n.adjacencyList[j])
                fw.write(++k + " "); // increment by 1 so node ids start at 1
            fw.write("\n");
        }

        fw.flush();
        fw.close();
    }

    /**
     * write ONE INDEXED neighborhoods concat'd into single file
     */
    @Override
    public void save(String path) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path)))) {
            for(Neighborhood n : neighborhoods) {
                bw.write(n.getCentralNode() + "\n");
                bw.write(n.numNodes() + " " + n.numEdges() / 2 + "\n");

                for (int i = 0; i < n.adjacencyList.length; i++) {
                    for (int j : n.adjacencyList[i])
                        bw.write(++j + " "); // increment by 1 so node ids start at 1
                    bw.write("\n");
                }

                bw.write("\n");
            }
        }
    }

    /**
     * Basic graph class implemented via adjacency list
     */
    public static class Neighborhood {
        ArrayList<Integer>[] adjacencyList;
        int e;
        int centralNode;

        HashMap<Integer, Integer> idMap = new HashMap<>();
        HashMap<Integer, Integer> reverseIdMap = new HashMap<>();
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
            e++;
        }

        public void mapId(int old) {
            idMap.put(old, currentMaxId);
            reverseIdMap.put(currentMaxId, old);
            currentMaxId += 1;
        }

        public int getCentralNode() {
            return centralNode;
        }

        public int numNodes() {
            return adjacencyList.length;
        }

        public int numEdges() {
            return e;
        }
    }
}
