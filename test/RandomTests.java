import java.io.*;

public class RandomTests {
    public static void main(String[] args) throws Exception {
        String name = "JPyFifo";
        ProcessBuilder pb = new ProcessBuilder();
        String dir = System.getProperty("user.dir") + "/";
        pb.command("rm", dir + name);
        pb.inheritIO().start();
        pb.command("mkfifo", dir + name);
        pb.inheritIO().start();
    }
}
