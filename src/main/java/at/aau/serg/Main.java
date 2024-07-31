package at.aau.serg;

import at.aau.serg.cli.CliUtils;
import at.aau.serg.javaparser.JParser;
import at.aau.serg.javaparser.MethodNotFoundException;
import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.AnalysisBuilder;
import at.aau.serg.soot.SootAnalysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import org.apache.commons.cli.*;

import java.io.IOException;
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
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(classPath, className, methodName));
        Analysis analysis = analysisBuilder.fullAnalysis();

        Set<AnalysisResult> resultSet = analysis.analyse();

        System.out.println("----- Analysis completed -----");
        System.out.println(resultSet);

        JParser jParser;
        try {
            jParser = new JParser(sourcePath, className, methodName);
        } catch (IOException | MethodNotFoundException e) {
            throw new RuntimeException(e);
        }

        jParser.parse(resultSet);

        try {
            jParser.export(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}