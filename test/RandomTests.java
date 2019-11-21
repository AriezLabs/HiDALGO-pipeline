import java.io.*;

public class RandomTests {
    public static void main(String[] args) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(new File("data/pokec.metis")))) {
            StreamTokenizer st = new StreamTokenizer(br);
            st.eolIsSignificant(true);

            System.out.println(br.readLine());
            while(st.nextToken() != StreamTokenizer.TT_EOF) {
                if(st.ttype == StreamTokenizer.TT_EOL)
                    System.out.println("eol");
                int casted = (int) st.nval;
                int rounded = (int) (st.nval + 0.5);
                if(casted != rounded)
                    System.out.println(casted + " - " + rounded);
            }
        }
    }
}
