import log.Log;
import plan.ExecutionPlan;
import excption.BadConfigException;
import excption.BadInputFormatException;
import type.DataType;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            Log.error("exactly one param required: path to execution plan");
            System.exit(1);
        }

        ExecutionPlan plan;
        String planName = args[0].split("/")[args[0].split("/").length - 1];

        Log.setCurrentLogLevel(Log.INFO);

        try {
            plan = new ExecutionPlan(args[0]);
            Log.info("successfully parsed " + plan.numStages() + "-stage execution plan " + planName);
            plan.execute();
            DataType result = plan.getCurrentData();
            if(result != null)
                result.save("./result_from_" + planName + "_at_" + System.currentTimeMillis());
        }
        catch (BadConfigException e) {
            Log.error("Bad execution plan!");
            Log.info("Execution plan syntax is specified in the docs at");
            Log.info("https://www.notion.so/hidalgoproject/12f7be2fc40b4cc58e9d31c58a51cbda?v=5ab4fd1e02a648ff8dd89d4ad5e1c8e8");
            e.printStackTrace();
            System.exit(1);
        }
        catch (BadInputFormatException e) {
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
