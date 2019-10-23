import process.BadConfigException;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0)
            usage();

        try {
            ExecutionPlan plan = new ExecutionPlan(args[0]);
            plan.execute();
        } catch (BadConfigException e) {
            configError(e);
        }
    }

    private static void usage() {
        Log.error("missing required param: path to execution plan");
        System.exit(1);
    }

    private static void configError(BadConfigException e) {
        Log.error("Bad execution plan!");
        Log.error("Execution plan syntax is specified in the docs at");
        Log.error("https://www.notion.so/hidalgoproject/12f7be2fc40b4cc58e9d31c58a51cbda?v=5ab4fd1e02a648ff8dd89d4ad5e1c8e8");
        e.printStackTrace();
        System.exit(1);
    }
}
