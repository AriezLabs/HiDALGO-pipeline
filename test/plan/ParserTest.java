package plan;

import log.Log;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Scanner;

class ParserTest {
    @Test
    void test() throws Exception{
        PipelineStage[] result = Parser.parse(new StringReader("# Plan for completely processing a graph into a list of its communities.\n" +
                "\n" +
                "ReadInput {\n" +
                "\tMetis resources/pokec.metis\n" +
                "}\n" +
                "\n" +
                "PLM {\n" +
                "\tMinCCSize 5\n" +
                "\tMinCCEdges 10\n" +
                "}\n" +
                "\n" +
                "RemoveSubsets {}\n" +
                "\n" +
                "# Get rid of starlike graphs.\n" +
                "FilterStarlike {\n" +
                "\tStronglyConnectedDegree 0.8\n" +
                "\tStronglyConnectedMaxAmount 0.2\n" +
                "\tLargestCCMaxSize 0.2\n" +
                "}\n" +
                "\n" +
                "Merge {\n" +
                "\tNodeOverlapThreshold 0.5\n" +
                "\tEdgeOverlapThreshold 2\n" +
                "}"));
    }
}