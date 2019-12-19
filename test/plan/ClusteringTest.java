package plan;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

public class ClusteringTest {

    private class Result {
        Set<Integer> set;

        public Result(String s) {
            set = new HashSet<>();
            for (String i: s.split(" ")) {
                set.add(Integer.parseInt(i));
            }
        }

        public boolean compare(Result o) {
            for(int i : set)
                if(!o.set.contains(i))
                    return false;

            return o.set.size() == set.size();
        }

        public String toString() {
            return set.toString();
        }
    }

    @Test
    void test() throws Exception {
        ExecutionPlan ep = new ExecutionPlan("data/test/clusteringtest.ep");
        ep.execute();
        try (BufferedReader br = new BufferedReader(new FileReader(new File("data/test/clusteringtest.nl")))) {
            String line;
            String[] correctResults = {
                    "1 2 3 4",
                    "1 5 6",
                    "2 1 3 4",
                    "3 1 2 4",
                    "4 1 2 3",
                    "5 1 6",
                    "6 1 5",
            };

            Result[] correct = new Result[correctResults.length];
            for (int i = 0; i < correctResults.length; i++) {
                correct[i] = new Result(correctResults[i]);
            }

            outer: while ((line = br.readLine()) != null) {
                Result n = new Result(line.trim());
                for (int i = 0; i < correct.length; i++) {
                    if(correct[i] != null && correct[i].compare(n)) {
                        correct[i] = null;
                        continue outer;
                    }
                }
                fail("algo produces unexpected community " + n.set);
            }

            for (Result result : correct) {
                if (result != null)
                    fail("algo output is missing community " + result.set);
            }

            System.out.println("SUCCESS: exact match");
        }
    }

}
