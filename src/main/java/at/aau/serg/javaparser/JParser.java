package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.soot.analysisTypes.StaticVariableWrite;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import javassist.expr.MethodCall;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    public void parse(Set<AnalysisResult> results) {
        for (AnalysisResult result : results) {
            if(result instanceof StaticMethodCall) {
                parseStaticMethodCall((StaticMethodCall) result);
            } else if (result instanceof StaticVariableReference) {
                parseStaticVariableReference((StaticVariableReference) result);
            } else if (result instanceof StaticVariableWrite) {
                parseStaticVariableWrite((StaticVariableWrite) result);
            }
        }
    }

    private void parseStaticVariableWrite(StaticVariableWrite staticVariableWrite) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void parseStaticVariableReference(StaticVariableReference staticVariableReference) {
        if(!method.getBody().isPresent()) return;

        AtomicInteger c = new AtomicInteger();
        method.getBody().get().findAll(FieldAccessExpr.class).forEach(fae -> {
            if(match(fae, staticVariableReference)){
                // Replace
                System.out.println("Replacing static variable " + fae);

                String newVariableName = staticVariableReference.getClassName()+staticVariableReference.getVariableName()+c;
                Parameter newParam = new Parameter(staticVariableReference.getReturnTypeAsJavaParserType(), newVariableName);
                method.addParameter(newParam);
                fae.replace(new NameExpr(newVariableName));
                c.getAndIncrement();
            }
        });

        changeMethodCalls();
    }

    private void parseStaticMethodCall(StaticMethodCall staticMethodCall) {
        if(!method.getBody().isPresent()) return;
        String newParameterName = staticMethodCall.getClassName()+staticMethodCall.getMethodName();
        method.addParameter(staticMethodCall.getReturnTypeAsJavaParserType(),newParameterName);
        // remove all calls to method with new parameter
        method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
            if (callExpr.getScope().isPresent() && callExpr.getScope().get().toString().equals(staticMethodCall.getClassName())) {
                if (callExpr.getNameAsString().equals(staticMethodCall.getMethodName())) {
                    // Jackpot
                    System.out.printf("Replacing static method call %s%n", staticMethodCall.getCallString());
                    callExpr.replace(new NameExpr(newParameterName));
                }
            }
        });
        // TODO: Refactor this
        changeMethodCalls();
    }

    private void changeMethodCalls() {
        compilationUnit.findAll(MethodCallExpr.class).forEach(callExpr -> {
            if(callExpr.getName().equals(method.getName())) {
                callExpr.replace(
                        new MethodCallExpr(
                                callExpr.getScope().orElse(null),
                                callExpr.getNameAsString(),
                                new NodeList<Expression>(
                                        method.getParameters()
                                                .stream().map(JParser::convertParameter)
                                                .collect(Collectors.toList()))));
            }
        });
    }


    public void export(String outputPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        System.out.printf("\nExporting new file: \"%s\"%n", outputPath);
        writer.write(compilationUnit.toString());
        writer.flush();
        writer.close();
    }

    private boolean match(FieldAccessExpr fae, StaticVariableReference ref) {
        return ref.getClassName().equals(fae.getScope().toString()) && ref.getVariableName().equals(fae.getNameAsString());
    }

    protected static Expression convertParameter(Parameter parameter) {
        switch (parameter.getType().toString()) {
            case "boolean":
                return new BooleanLiteralExpr(true);
            case "byte":
            case "short":
            case "int":
                return new IntegerLiteralExpr("1");
            case "long":
                return new LongLiteralExpr("1");
            case "float":
            case "double":
                return new DoubleLiteralExpr("1");
            case "char":
                return new StringLiteralExpr("1");
        }
        return null;
    }
}
