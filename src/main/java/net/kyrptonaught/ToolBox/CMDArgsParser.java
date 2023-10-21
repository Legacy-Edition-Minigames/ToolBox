package net.kyrptonaught.ToolBox;


public class CMDArgsParser {

    private static String[] args;

    public static void setArgs(String[] args) {
        CMDArgsParser.args = args;
    }

    public static boolean containsArgs(String arg) {
        for (String str : args) {
            if (str.equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

    public static boolean containsOrBeginsArgs(String arg) {
        for (String str : args) {
            if (str.equalsIgnoreCase(arg) || str.startsWith(arg)) return true;
        }
        return false;
    }

    public static boolean containsServer() {
        return containsArgs("--server");
    }

    public static String getTargetServer() {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equalsIgnoreCase("--server") && !args[i + 1].startsWith("--")) {
                return args[i + 1];
            }
        }
        return null;
    }

    public static boolean updateServer() {
        return containsArgs("--updateServer");
    }

    public static boolean launchServer() {
        return containsArgs("--launchServer");
    }

    public static boolean skipSplash() {
        return containsArgs("--skipSplash");
    }

    public static boolean autoExit() {
        return containsArgs("--autoExit");
    }

    public static boolean autoRestart() {
        return containsArgs("--autoRestart");
    }

    public static boolean updateAll() {
        return containsArgs("--updateAll");
    }
}
