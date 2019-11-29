import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class RandomTests {
    public static void main(String[] args) throws Exception {
        Process p = new ProcessBuilder("python", "/home/ariez/Projects/HiDALGO-pipeline/test/testPyFile.py").inheritIO().start();

        try {
            File f_pipe = new File ( "/tmp/test" );
            RandomAccessFile raf = new RandomAccessFile(f_pipe, "r");  // point 1
            for(;;){
                String line = raf.readLine(); //point 2
                //Take care to check the line -
                System.out.println(line);
                //it is null when the pipe has no more available data.
                if( line==null )  break; //point 3
            }
            System.out.println("PIPE DOWN");
        }
        catch (Exception e) {
            // do something
        }
    }
}
