package at.aau.serg.cli;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CliUtils {
    private static final HelpFormatter helpFormatter = new HelpFormatter();

    public static Options getOptions() {
        return new Options()
                .addOption("s", "src", true, "location of source file")
                .addOption("c", "class", true, "location of class file")
                .addOption("m", "method", true, "method to be analyzed")
                .addOption("o", "output", true, "output file");
    }

    public static void printHelp() {
        helpFormatter.printHelp("USAGE: java -jar sympreproc.jar", getOptions());
    }
}
