package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.*;
import at.aau.serg.utils.TypeAdapter;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import sootup.core.types.VoidType;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JParser {
    private final CompilationUnit compilationUnit;
    private final MethodDeclaration method;

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

    /**
     * Constructor for testing
     * @param compilationUnit
     * @param method
     */
    public JParser(CompilationUnit compilationUnit, MethodDeclaration method) {
        this.compilationUnit = compilationUnit;
        this.method = method;
    }

    /**
     * For testing purposes
     * @return the compilation unit of the parsed class
     */
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    /**
     * For testing purposes
     * @return the method that should be parsed
     */
    public MethodDeclaration getMethod() {
        return method;
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
                        throw new UnsupportedOperationException("Unsupported reference type: " + ref.getReferenceType());
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
                        throw new UnsupportedOperationException("Unsupported reference type: " + ref.getReferenceType());
                }
            } else if (result instanceof MarkedMethod) {
                MarkedMethod markedMethod = (MarkedMethod) result;
                parseMarkedMethodCall(markedMethod);
            }
        }

        // Clean up and remove unused method parameters
        Predicate<Parameter> isUsed = p -> !method.findAll(NameExpr.class,n -> n.getNameAsString().equals(p.getNameAsString())).isEmpty();

        method.getParameters().removeIf(isUsed.negate());
        System.out.println("----- Finished parsing -----");
    }

    private void parseMarkedMethodCall(MarkedMethod m) {
        
        Predicate<MethodCallExpr> isMarkedMethodCall = mc -> mc.getNameAsString().equals(m.getMethodName()) && (m.hasVarArgs() || mc.getArguments().size() == m.getParameterTypes().size());

        AtomicInteger counter = new AtomicInteger(0);

        Consumer<MethodCallExpr> transform = mc -> {
            processMethodArguments(m, mc, counter);

            if(!(m.getReturnType().asSootType() instanceof VoidType)) {
                Parameter newParam = new Parameter(m.getReturnType().asJavaParserType(), m.getNewVariableName() + "_ret");
                if(method.getParameters().stream().noneMatch(p -> p.equals(newParam))) {
                    method.addParameter(newParam);
                }
                if(mc.getParentNode().get() instanceof ExpressionStmt) {
                    mc.getParentNode().get().remove();
                } else {
                    mc.replace(new NameExpr(newParam.getNameAsString()));
                }
            } else {
                Node node = mc;
                while(!node.remove()) node = node.getParentNode().orElseThrow(() -> new IllegalStateException("Unable to delete initial call"));
            }

            counter.getAndIncrement();
        };


        method.findAll(MethodCallExpr.class).stream()
                .filter(isMarkedMethodCall)
                .forEach(transform);
    }

    private void processMethodArguments(MarkedMethod m, MethodCallExpr mc, AtomicInteger counter) {
        NodeList<Statement> assignStmts = new NodeList<>();
        for(int i = 0; i < mc.getArguments().size(); i++) {
            Expression arg = mc.getArguments().get(i);
            String variableName = m.getNewVariableName() + counter.get() + "_arg" + i;
            assignStmts.add(new ExpressionStmt(new AssignExpr(new NameExpr(variableName), arg, AssignExpr.Operator.ASSIGN)));

            if(m.hasVarArgs() && i >= m.getParameterTypes().size()-1) {
                // now we have reached the varargs
                TypeAdapter argType;
                if(arg instanceof LiteralExpr) {
                    argType = new TypeAdapter(arg.asLiteralExpr());
                } else {
                    argType = new TypeAdapter(arg.calculateResolvedType());
                }
                method.getBody().get().getStatements().add(0, new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(argType.asJavaParserType(), variableName, argType.getDefaultJavaParserExpression()))));
            }else {
                method.getBody().get().getStatements().add(0, new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(m.getParameterTypes().get(i).asJavaParserType(), variableName, m.getParameterTypes().get(i).getDefaultJavaParserExpression()))));
            }
            changeReturnTypeToList();
            changeReturnStatements(variableName);
        }
        assert mc.getParentNode().isPresent() && mc.getParentNode().isPresent() && mc.getParentNode().get().getParentNode().isPresent();
        BlockStmt blockStmt = findEnclosingBlockStatement(mc).orElseThrow(() -> new IllegalStateException("No enclosing BlockStatement"));
        int index = getIndex(blockStmt, mc);
        blockStmt.getStatements().addAll(index, assignStmts);
    }

    private int getIndex(BlockStmt blockStmt, MethodCallExpr mc) {
        Node n = mc.getParentNode().get();
        while(!blockStmt.getStatements().contains(n)) n = n.getParentNode().get();
        return blockStmt.getStatements().indexOf(n);
    }

    private Optional<BlockStmt> findEnclosingBlockStatement(Node node) {
        while(node.getParentNode().isPresent()) {
            node = node.getParentNode().get();
            if(node instanceof BlockStmt) {
                return Optional.of((BlockStmt) node);
            }
        }
        return Optional.empty();
    }

    private void parseObjectFieldRead(ObjectFieldReference ref) {
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(ref.getNewVariableName()) && p.getType().equals(ref.getType().asJavaParserType()))) {
            Parameter newParam = new Parameter(ref.getType().asJavaParserType(), ref.getNewVariableName());
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
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(ref.getNewVariableName()) && p.getType().equals(ref.getType().asJavaParserType()))) {
            Parameter newParam = new Parameter(ref.getType().asJavaParserType(), ref.getNewVariableName());
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
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(staticVariableWrite.getNewVariableName()) && p.getType().equals(staticVariableWrite.getType().asJavaParserType()))) {
            Parameter newParam = new Parameter(staticVariableWrite.getType().asJavaParserType(), staticVariableWrite.getNewVariableName());
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
        if(method.getParameters().stream().noneMatch(p -> p.getNameAsString().equals(staticVariableReference.getNewVariableName()) && p.getType().equals(staticVariableReference.getType().asJavaParserType()))) {
            Parameter newParam = new Parameter(staticVariableReference.getType().asJavaParserType(), staticVariableReference.getNewVariableName());
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
        method.addParameter(staticMethodCall.getReturnType().asJavaParserType(), staticMethodCall.getNewVariableName());
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
                                new NodeList<>(
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
        if(method.getType().toString().equals("ReturnValues")) return;
        method.setType(StaticJavaParser.parseClassOrInterfaceType("ReturnValues"));
    }

    private void changeReturnStatements(String newVariableName) {
        if(method.getBody().get().findAll(ReturnStmt.class).isEmpty()) {
            method.getBody().get().getStatements().add(new ReturnStmt(createObjectCreationExpr(new NameExpr(newVariableName))));
            return;
        }
        method.getBody().get().findAll(ReturnStmt.class).forEach(returnStmt -> {
            if(returnStmt.getExpression().isPresent()) {
                Expression exp = returnStmt.getExpression().get();
                if(isObjectCreationExpr(exp)) {
                    ObjectCreationExpr old = exp.asObjectCreationExpr();
                    NodeList<Expression> args = old.getArguments();
                    args.add(new NameExpr(newVariableName));
                    ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(old.getScope().orElse(null), old.getType(), args);
                    old.replace(objectCreationExpr);
                } else {
                    returnStmt.setExpression(createObjectCreationExpr(exp, new NameExpr(newVariableName)));
                }
            } else {
                returnStmt.setExpression(createObjectCreationExpr(new NameExpr(newVariableName)));
            }
        });
    }

    private boolean isObjectCreationExpr(Expression exp) {
        return exp instanceof ObjectCreationExpr
                && ((ObjectCreationExpr) exp).getType().toString().equals("ReturnValues");
    }

    private ObjectCreationExpr createObjectCreationExpr(Expression... expressions) {
        return new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType("ReturnValues"),new NodeList<>(expressions));
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
