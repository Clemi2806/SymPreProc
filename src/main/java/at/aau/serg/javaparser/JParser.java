package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.*;
import java.nio.file.Files;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
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

        method = compilationUnit
                .findAll(MethodDeclaration.class)
                .stream().filter(md -> md.getNameAsString().equals(methodName))
                .findFirst()
                .orElseThrow(() -> new MethodNotFoundException("Method not found: " + methodName));

    }

    public void parse(Set<AnalysisResult> results) {
        System.out.println("----- Starting parsing -----");
        if(!method.getBody().isPresent()) throw new IllegalStateException("Unable to load method body");
        for (AnalysisResult result : results) {
            if(result instanceof StaticMethodCall) {
                parseStaticMethodCall((StaticMethodCall) result);
            } else if (result instanceof StaticVariableReference) {
                StaticVariableReference ref = (StaticVariableReference) result;
                switch (ref.getReferenceType()) {
                    case READ:
                        parseStaticVariableReference(ref);
                        break;
                    case WRITE:
                        parseStaticVariableWrite(ref);
                        break;
                    default:
                        new UnsupportedOperationException("Unsupported reference type: " + ref.getReferenceType());
                        break;
                }
            } else if (result instanceof ObjectFieldReference) {
                ObjectFieldReference ref = (ObjectFieldReference) result;
                switch (ref.getReferenceType()) {
                    case READ:
                        parseObjectFieldRead(ref);
                        break;
                    case WRITE:
                        parseObjectFieldWrite(ref);
                        break;
                    default:
                        new UnsupportedOperationException("Unsupported reference type: " + ref.getReferenceType());
                        break;
                }
            }
        }
        System.out.println("----- Finished parsing -----");
    }

    private void parseObjectFieldRead(ObjectFieldReference ref) {
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(ref.getNewVariableName()) && p.getType().equals(ref.getReturnTypeAsJavaParserType()))) {
            Parameter newParam = new Parameter(ref.getReturnTypeAsJavaParserType(), ref.getNewVariableName());
            method.addParameter(newParam);
        }

        Predicate<FieldAccessExpr> isFieldRead = fae -> {
            if(!fae.hasParentNode()) return true;
            if(!(fae.getParentNode().get() instanceof AssignExpr)) return true;
            return !((AssignExpr) fae.getParentNode().get()).getTarget().equals(fae);
        };
        Predicate<FieldAccessExpr> isObjectUnderInspection = fae -> fae.getScope().toString().equals(ref.getObjectName()) && fae.getNameAsString().equals(ref.getFieldName());
        Consumer<FieldAccessExpr> replace = fae -> fae.replace(new NameExpr(ref.getNewVariableName()));

        method.findAll(FieldAccessExpr.class).stream()
                .filter(isFieldRead)
                .filter(isObjectUnderInspection)
                .forEach(replace);
    }

    private void parseObjectFieldWrite(ObjectFieldReference ref) {
        //throw new UnsupportedOperationException("Not implemented yet");
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(ref.getNewVariableName()) && p.getType().equals(ref.getReturnTypeAsJavaParserType()))) {
            Parameter newParam = new Parameter(ref.getReturnTypeAsJavaParserType(), ref.getNewVariableName());
            method.addParameter(newParam);
        }

        Predicate<FieldAccessExpr> isObjectUnderInspection = fae -> fae.getScope().toString().equals(ref.getObjectName()) && fae.getNameAsString().equals(ref.getFieldName());
        Consumer<FieldAccessExpr> replace = fae -> fae.replace(new NameExpr(ref.getNewVariableName()));

        method.findAll(FieldAccessExpr.class).stream() // TODO: Add filter to only get writes
                .filter(isObjectUnderInspection)
                .forEach(replace);

        changeReturnTypeToList();
        changeReturnStatements(ref.getNewVariableName());
    }

    private void parseStaticVariableWrite(StaticVariableReference staticVariableWrite) {

        // Add new parameter if parameter does not exist yet
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(staticVariableWrite.getNewVariableName()) && p.getType().equals(staticVariableWrite.getReturnTypeAsJavaParserType()))) {
            Parameter newParam = new Parameter(staticVariableWrite.getReturnTypeAsJavaParserType(), staticVariableWrite.getNewVariableName());
            method.addParameter(newParam);
        }

        Predicate<FieldAccessExpr> isAccessOnVariable = fieldAccessExpr -> fieldAccessExpr.getScope().toString().equals(staticVariableWrite.getClassName()) && fieldAccessExpr.getNameAsString().equals(staticVariableWrite.getVariableName());
        Consumer<FieldAccessExpr> replaceAccessExpr = fieldAccessExpr -> fieldAccessExpr.replace(new NameExpr(staticVariableWrite.getNewVariableName()));


        method.getBody().get().findAll(AssignExpr.class).stream()
                .map(AssignExpr::getTarget)
                .filter(Expression::isFieldAccessExpr)
                .map(Expression::asFieldAccessExpr)
                .filter(isAccessOnVariable)
                .forEach(replaceAccessExpr);

        changeReturnTypeToList();
        changeReturnStatements(staticVariableWrite.getNewVariableName());
    }

    // TODO: Fix method pipeline to only lookup static variable reads not writes
    private void parseStaticVariableReference(StaticVariableReference staticVariableReference) {
        // Add new parameter if parameter does not exist yet
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(staticVariableReference.getNewVariableName()) && p.getType().equals(staticVariableReference.getReturnTypeAsJavaParserType()))) {
            Parameter newParam = new Parameter(staticVariableReference.getReturnTypeAsJavaParserType(), staticVariableReference.getNewVariableName());
            method.addParameter(newParam);
        }

        method.getBody().get().findAll(FieldAccessExpr.class).forEach(fae -> {
            if(isMatch(fae, staticVariableReference)){
                // Replace
                System.out.println("Replacing static variable " + fae);
                fae.replace(new NameExpr(staticVariableReference.getNewVariableName()));
            }
        });

        changeMethodCalls();
    }

    private void parseStaticMethodCall(StaticMethodCall staticMethodCall) {
        method.addParameter(staticMethodCall.getReturnTypeAsJavaParserType(), staticMethodCall.getNewVariableName());
        // remove all calls to method with new parameter
        method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
            if (callExpr.getScope().isPresent() && callExpr.getScope().get().toString().equals(staticMethodCall.getClassName())) {
                if (callExpr.getNameAsString().equals(staticMethodCall.getMethodName())) {
                    // Jackpot
                    System.out.printf("Replacing static method call %s%n", staticMethodCall.getCallString());
                    callExpr.replace(new NameExpr(staticMethodCall.getNewVariableName()));
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

    private boolean isMatch(FieldAccessExpr fae, StaticVariableReference ref) {
        return ref.getClassName().equals(fae.getScope().toString()) && ref.getVariableName().equals(fae.getNameAsString());
    }

    private void changeReturnTypeToList() {
        if(method.getType().toString().equals("Object[]")) return;
        method.setType(new ArrayType(new ClassOrInterfaceType(null,"Object")));
    }

    private void changeReturnStatements(String newVariableName) {
        if(method.getBody().get().findAll(ReturnStmt.class).isEmpty()) {
            method.getBody().get().getStatements().add(new ReturnStmt(createObjectArrayCreationExpr(new NameExpr(newVariableName))));
            return;
        }
        method.getBody().get().findAll(ReturnStmt.class).forEach(returnStmt -> {
            if(returnStmt.getExpression().isPresent()) {
                Expression exp = returnStmt.getExpression().get();
                if(isObjectArrayCreationExpr(exp)) {
                    ((ArrayCreationExpr) exp).getInitializer().get().getValues().add(new NameExpr(newVariableName));
                } else {
                    returnStmt.setExpression(createObjectArrayCreationExpr(exp, new NameExpr(newVariableName)));
                }
            } else {
                returnStmt.setExpression(createObjectArrayCreationExpr(new NameExpr(newVariableName)));
            }
        });
    }

    private boolean isObjectArrayCreationExpr(Expression exp) {
        return exp instanceof ArrayCreationExpr
                && ((ArrayCreationExpr) exp).getElementType().toString().equals("Object")
                && ((ArrayCreationExpr) exp).getInitializer().isPresent();
    }

    private ArrayCreationExpr createObjectArrayCreationExpr(Expression... expressions) {
        return new ArrayCreationExpr(
                new ClassOrInterfaceType(null, "Object"),
                NodeList.nodeList(new ArrayCreationLevel()),
                new ArrayInitializerExpr(NodeList.nodeList(expressions))
        );
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
