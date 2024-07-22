package at.aau.serg;

import at.aau.serg.cli.CliUtils;
import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(CliUtils.getOptions(), args);
        } catch (ParseException e) {
            CliUtils.printHelp();
            System.exit(1);
        }

        String sourceFile = cmd.getOptionValue("source");
        String outputFile = cmd.getOptionValue("output");
        String classFile = cmd.getOptionValue("class");
        String methodName = cmd.getOptionValue("method");

        if(sourceFile == null) {
            System.err.println("Source file required");
        }
        if(outputFile == null) {
            System.err.println("Output file required");
        }
        if(classFile == null) {
            System.err.println("Class file required");
        }
        if(methodName == null) {
            System.err.println("Method name required");
        }


    }
}