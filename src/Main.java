import log.Log;
import plan.ExecutionPlan;
import process.exception.BadConfigException;
import process.exception.BadInputFormatException;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            Log.error("missing required param: path to execution plan");
            System.exit(1);
        }

        ExecutionPlan plan;

        try {
            plan = new ExecutionPlan(args[0]);
            plan.execute();

        } catch (BadConfigException e) {
            Log.error("Bad execution plan!");
            Log.info("Execution plan syntax is specified in the docs at");
            Log.info("https://www.notion.so/hidalgoproject/12f7be2fc40b4cc58e9d31c58a51cbda?v=5ab4fd1e02a648ff8dd89d4ad5e1c8e8");
            e.printStackTrace();
            System.exit(1);

        } catch (BadInputFormatException e) {
            Log.error("Bad pipeline! Wrong input format passed to stage:");
            e.printStackTrace();

            Scanner s = new Scanner(System.in);
            String in;
            do {
                System.out.print("Save current data? [y/n]:");
                in = s.nextLine();
            } while (!"yn".contains(in) && in.length() != 1);
            if (in.equals("y")) {
                System.out.println("not actually implemented yet");
                //TODO IMPLEMENT EMERGENCY SAVE
            }
        }
    }
}
