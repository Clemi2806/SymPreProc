package at.aau.serg;

import at.aau.serg.cli.CliUtils;
import at.aau.serg.javaparser.JParser;
import at.aau.serg.javaparser.MethodNotFoundException;
import at.aau.serg.javaparser.StaticCallParser;
import at.aau.serg.soot.SootAnalysis;
import at.aau.serg.soot.StaticMethodCall;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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

        String sourcePath = cmd.getOptionValue("sourcepath");
        String outputFile = cmd.getOptionValue("output");
        String classPath = cmd.getOptionValue("classpath");
        String methodSpecifier = cmd.getOptionValue("method");

        if(sourcePath == null) {
            System.err.println("Source file required");
            System.exit(1);
        }
        if(outputFile == null) {
            System.err.println("Output file required");
            System.exit(1);
        }
        if(classPath == null) {
            System.err.println("Class file required");
            System.exit(1);
        }
        if(methodSpecifier == null) {
            System.err.println("Method specifier required");
            System.exit(1);
        }

        String methodName = methodSpecifier.substring(methodSpecifier.lastIndexOf(".")+1);
        String className = methodSpecifier.substring(0, methodSpecifier.lastIndexOf("."));

        System.out.println("----- Starting analysis -----");
        SootAnalysis analysis = new SootAnalysis(classPath, className, methodName);

        StaticCallParser staticCallParser = new StaticCallParser(analysis.getStaticMethodCalls());

        JParser jParser;
        try {
            jParser = new JParser(sourcePath, className, methodName);
        } catch (IOException | MethodNotFoundException e) {
            throw new RuntimeException(e);
        }

        System.out.println("----- Parsing static method calls -----");

        jParser.parseMethod(staticCallParser);

        try {
            jParser.export(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}