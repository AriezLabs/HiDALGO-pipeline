package log;

public class Log {
    public static final long startTime = System.currentTimeMillis();

    private static int currentLogLevel = 1;

    public static final int DEBUG = 0;
    public static final int INFO = 1;
    public static final int WARNING = 2;
    public static final int ERROR = 3;

    // from https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void debug(Object stuff) {
        log(stuff, DEBUG);
    }

    public static void debug(Object stuff, int indentLevel) {
        log(stuff, DEBUG, indentLevel);
    }

    public static void warn(Object stuff) {
        log(stuff, WARNING);
    }

    public static void warn(Object stuff, int indentLevel) {
        log(stuff, WARNING, indentLevel);
    }

    public static void error(Object stuff) {
        log(stuff, ERROR);
    }

    public static void error(Object stuff, int indentLevel) {
        log(stuff, ERROR, indentLevel);
    }

    public static void info(Object stuff) {
        log(stuff, INFO);
    }

    public static void info(Object stuff, int indentLevel) {
        log(stuff, INFO, indentLevel);
    }

    public static void log(Object stuff) {
        log(stuff, 0);
    }

    public static void log(Object stuff, int logLevel) {
        log(stuff, logLevel, 0);
    }

    public static synchronized void log(Object stuff, int logLevel, int indentLevel) {
        if(currentLogLevel <= logLevel) {
            System.out.printf("[%.3f ", 0.001d * (System.currentTimeMillis() - startTime));

            switch(logLevel) {
                case DEBUG:
                    System.out.printf("%sDEBUG%s]\t", ANSI_BLUE, ANSI_RESET);
                    break;
                case INFO:
                    System.out.printf("%sINFO%s]\t", ANSI_GREEN, ANSI_RESET);
                    break;
                case WARNING:
                    System.out.printf("%sWARN%s]\t", ANSI_YELLOW, ANSI_RESET);
                    break;
                case ERROR:
                    System.out.printf("%sERROR%s]\t", ANSI_RED, ANSI_RESET);
            }

            if (indentLevel > 0) {
                while (indentLevel > 1) {
                    System.out.print("\t");
                    indentLevel--;
                }
                System.out.print("└");
                indentLevel = indentLevel * 4;
                for (int i = 1; i < indentLevel; i++)
                    System.out.print("─");
            }

            System.out.println(stuff.toString());
        }
    }

    public static void setCurrentLogLevel(int logLevel) {
        currentLogLevel = logLevel;
    }
}
