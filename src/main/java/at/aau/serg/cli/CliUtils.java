package at.aau.serg.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class CliUtils {
    private static final HelpFormatter helpFormatter = new HelpFormatter();

    public static Options getOptions() {
        return new Options()
                .addOption("s", "sourcepath", true, "sourcepath")
                .addOption("c", "classpath", true, "classpath")
                .addOption("m", "method", true, "fully specified methodname, like org.example.A.method1")
                .addOption("o", "output", true, "output file")
                .addOption("k", "config", true, "config file");
    }

    public static void printHelp() {
        helpFormatter.printHelp("USAGE: java -jar sympreproc.jar", getOptions());
    }
}
