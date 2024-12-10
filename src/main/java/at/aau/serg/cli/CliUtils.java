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
                .addOption("k", "config", true, "config file")
                .addOption("1", "disable-smc", false,"disable static method call analysis")
                .addOption("2", "disable-svr", false,"disable static variable read analysis")
                .addOption("3", "disable-svw", false,"disable static variable write analysis")
                .addOption("4", "disable-ofw", false,"disable object field write analysis")
                .addOption("5", "disable-ofr", false,"disable object field read analysis")
                .addOption("6", "disable-mmc", false,"disable marked method call analysis");
    }

    public static void printHelp() {
        helpFormatter.printHelp("USAGE: java -jar sympreproc.jar", getOptions());
    }
}
