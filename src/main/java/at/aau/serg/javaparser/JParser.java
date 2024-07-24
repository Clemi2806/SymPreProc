package at.aau.serg.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.*;
import java.nio.file.Files;

public class JParser {
    private CompilationUnit compilationUnit;
    private MethodDeclaration method;

    public JParser(String sourcePath, String className, String methodName) throws IOException, MethodNotFoundException {
        String pathToClass = className.replaceAll("\\.", "/");
        if (!sourcePath.endsWith("/")) sourcePath += "/";

        String fullySpecifiedFilePath = sourcePath + pathToClass + ".java";

        File file = new File(fullySpecifiedFilePath);

        if(!file.exists()) throw new FileNotFoundException("File not found at: " + fullySpecifiedFilePath);

        compilationUnit = StaticJavaParser.parse(Files.newInputStream(file.toPath()));

        // TODO: Specify method using full signature, to filter out overloaded methods
        method = compilationUnit
                .findAll(MethodDeclaration.class)
                .stream().filter(md -> md.getNameAsString().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new MethodNotFoundException("Method not found: " + methodName));

    }

    public void parseMethod(MethodParser<?> methodParser) {
        methodParser.parse(method);
    }

    public void export(String outputPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        System.out.printf("\nExporting new file: \"%s\"%n", outputPath);
        writer.write(compilationUnit.toString());
        writer.flush();
        writer.close();
    }
}
