package at.aau.serg.javaparser;

import at.aau.serg.soot.StaticMethodCall;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.PrimitiveType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StaticCallParser implements MethodParser<StaticMethodCall>{
    Set<StaticMethodCall> parsingList;

    public StaticCallParser(Set<StaticMethodCall> parsingList) {
        this.parsingList = parsingList;
    }

    @Override
    public void parse(MethodDeclaration method) {
        if(!method.getBody().isPresent()) return;
        for(StaticMethodCall call : parsingList){
            // add new parameter to method parameter list
            String newParameterName = call.getClassName()+call.getMethodName();
            method.addParameter(call.getReturnTypeAsJavaParserType(),newParameterName);
            // remove all calls to method with new parameter
            method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
                if (callExpr.getScope().isPresent() && callExpr.getScope().get().toString().equals(call.getClassName())) {
                    if (callExpr.getNameAsString().equals(call.getMethodName())) {
                        // Jackpot
                        callExpr.replace(new NameExpr(newParameterName));
                    }
                }
            });
        }
        // TODO: Refactor this
        Optional<CompilationUnit> ocu = method.findCompilationUnit();
        if(!ocu.isPresent()) return;
        CompilationUnit cu = ocu.get();
        cu.findAll(MethodCallExpr.class).forEach(callExpr -> {
            if(callExpr.getName().equals(method.getName())) {
                callExpr.replace(new MethodCallExpr(callExpr.getScope().orElse(null), callExpr.getNameAsString(), new NodeList<Expression>(method.getParameters().stream().map(StaticCallParser::convertParameter).collect(Collectors.toList()))));
            }
        });
    }

    private static Expression convertParameter(Parameter parameter) {
        switch (parameter.getType().asPrimitiveType().toString()) {
            case "boolean":
                return new BooleanLiteralExpr(true);
            case "byte":
                return new IntegerLiteralExpr(1);
            case "short":
                return new IntegerLiteralExpr(1);
            case "int":
                return new IntegerLiteralExpr(1);
            case "long":
                return new LongLiteralExpr(1);
            case "float":
                return new DoubleLiteralExpr(1);
            case "double":
                return new DoubleLiteralExpr(1);
            case "char":
                return new StringLiteralExpr("1");
        }
        return null;
    }
}
