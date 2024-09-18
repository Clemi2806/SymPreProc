package at.aau.serg;

import at.aau.serg.cli.CliUtils;
import at.aau.serg.cli.Configurations;
import at.aau.serg.javaparser.JParser;
import at.aau.serg.javaparser.MethodNotFoundException;
import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.AnalysisBuilder;
import at.aau.serg.soot.SootAnalysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Configurations.reset();
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
        String configurationFilePath = cmd.getOptionValue("config");

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

        if(configurationFilePath != null) {
            Path configurationFile = Paths.get(configurationFilePath);
            if(!Files.exists(configurationFile)) {
                System.err.println("Configuration file does not exist");
                System.exit(1);
            }
            Configurations configurations;
            try {
                configurations = Configurations.getInstance(configurationFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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