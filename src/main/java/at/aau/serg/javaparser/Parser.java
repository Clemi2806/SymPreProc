package at.aau.serg.javaparser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;

public abstract class Parser {
    public abstract void parse(MethodDeclaration method);

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
