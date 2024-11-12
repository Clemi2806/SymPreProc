package at.aau.serg.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MethodCleanerTest {
    static MethodCleaner cleaner = new MethodCleaner();

    @Test
    void noUnusedParameters() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ int snippet(int a, int b) { return a+b; } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters, methodDeclaration.getParameters().size());
    }

    @Test
    void oneUnusedParameter() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ int snippet(int a, int b, int c) { return a+b; } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters-1, methodDeclaration.getParameters().size());
        assertFalse(methodDeclaration.getParameters().stream().anyMatch(p -> p.getNameAsString().equals("c")));
    }

    @Test
    void parameterInObjectDeclaration() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ int snippet(int a, int b, int c) { BigInteger bigInt = new BigInteger(c); return a+b; } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters, methodDeclaration.getParameters().size());
    }

    @Test
    void parameterInObjectDeclarationAddedDynamically() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ int snippet(int a, int b, int c) { return a+b; } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(new NameExpr("c"));

        ObjectCreationExpr newObject = new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType("BigInteger"), arguments);

        methodDeclaration.getBody().get().addStatement(0, newObject);

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters, methodDeclaration.getParameters().size());
    }

    @Test
    void parameterInObjectDeclarationInReturnStmt() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ int snippet(int a, int b, int c) { int x = a+b; return x; } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(new NameExpr("c"));

        ObjectCreationExpr newObject = new ObjectCreationExpr(null, StaticJavaParser.parseClassOrInterfaceType("BigInteger"), arguments);

        methodDeclaration.getBody().get().findAll(ReturnStmt.class).forEach(r -> r.setExpression(newObject));

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters, methodDeclaration.getParameters().size());
    }

    @Test
    void addParameterToExistingObjectCreationExpr() {
        CompilationUnit cu = StaticJavaParser.parse("class A{ void snippet(int a, int b, int c) { return new SomeObject(a,b); } }");
        MethodDeclaration methodDeclaration = cu.findFirst(MethodDeclaration.class).get();

        methodDeclaration.getBody().get().findAll(ObjectCreationExpr.class).forEach( o -> {
            o.getArguments().add(new NameExpr("c"));
        });

        int numberOfParameters = methodDeclaration.getParameters().size();

        cleaner.removeUnusedParameters(methodDeclaration);

        assertEquals(numberOfParameters, methodDeclaration.getParameters().size());
    }
}
